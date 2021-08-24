package fasar.cassandra;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Map;

public class VoidCSession implements Session {
    @Override
    public String getLoggedKeyspace() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public Session init() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ListenableFuture<Session> initAsync() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSet execute(String query) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSet execute(String query, Object... values) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSet execute(String query, Map<String, Object> values) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSet execute(Statement statement) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSetFuture executeAsync(String query) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSetFuture executeAsync(String query, Object... values) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSetFuture executeAsync(String query, Map<String, Object> values) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ResultSetFuture executeAsync(Statement statement) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public PreparedStatement prepare(String query) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public PreparedStatement prepare(RegularStatement statement) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ListenableFuture<PreparedStatement> prepareAsync(String query) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public ListenableFuture<PreparedStatement> prepareAsync(RegularStatement statement) {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public CloseFuture closeAsync() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public void close() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public boolean isClosed() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public Cluster getCluster() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }

    @Override
    public State getState() {
        throw new NotConnectedException("Not Connected To Cassandra");
    }
}
