package com.serverless;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A sample of using Lambda to connect RDS with plain jdbc without connection pooling.
 */
public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = Logger.getLogger(Handler.class);

    private static final String host = "mectest.cctwl58vu3o3.us-west-2.rds.amazonaws.com";
    private static final int port = 5432;
    private static final String database = "mectest";
    private static final String username = "mectest";
    private static final String password = "morrison";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: " + input);

        String message = "Go Serverless v1.x! Your function executed successfully!";

        Map<String, List<?>> data = new HashMap<>();

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, username,
                    password);
            stmt = conn.createStatement();
            String sql = "select * from information_schema.tables";
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            
            // key
            List<String> keys = Arrays.asList(new String[] {
                "table_catalog", "table_schema", "table_name"
            });
            data.put("keys", keys);
            
            // header
            List<Map<String, String>> head = new LinkedList<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String columnName = meta.getColumnName(i);
                String columnLabel = meta.getColumnLabel(i);
                Map<String, String> header = new HashMap<>();
                header.put("name", columnName);
                header.put("label", columnLabel);
                head.add(header);
            }
            data.put("head", head);

            // body
            List<Map<String, Object>> body = new LinkedList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String columnName = meta.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                body.add(row);
            }
            data.put("body", body);
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            message = e.getMessage();
        } catch (SQLException e) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            message = e.getMessage();
        }

        Response responseBody = new Response(message, input, data);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Credentials", "true");

        return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(responseBody)
                .setHeaders(headers).build();
    }
}
