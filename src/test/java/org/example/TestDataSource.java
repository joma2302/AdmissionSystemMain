package org.example;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Простий DataSource для тестів — повертає обгортку над спільним з'єднанням,
 * яка ігнорує виклик close(), щоб з'єднання залишалось відкритим між тестами.
 */
public class TestDataSource implements DataSource {
    private final Connection connection;

    public TestDataSource(Connection connection) {
        this.connection = connection;
    }

    private static Connection nonClosingProxy(Connection target) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName())) {
                        return null; // ігноруємо close()
                    }
                    return method.invoke(target, args);
                }
        );
    }

    @Override
    public Connection getConnection() throws SQLException {
        return nonClosingProxy(connection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return nonClosingProxy(connection);
    }

    @Override public PrintWriter getLogWriter() { return null; }
    @Override public void setLogWriter(PrintWriter out) {}
    @Override public void setLoginTimeout(int seconds) {}
    @Override public int getLoginTimeout() { return 0; }
    @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { throw new SQLFeatureNotSupportedException(); }
    @Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException(); }
    @Override public boolean isWrapperFor(Class<?> iface) { return false; }
}
