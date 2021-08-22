package fasar.cassandra;

import java.time.Instant;
import java.util.Objects;

/**
 * Give some details on the connexion with the current host.
 * <p>
 * The class allows to monitor number of open connections, active requests, and maximum capacity.
 */
public class CHostMonit {
    private final String host;
    private final Instant instant;
    private final int openConnexion;
    private final int currentLoad;
    private final int maxLoad;

    public CHostMonit(String host, Instant instant, int openConnexion, int currentLoad, int maxLoad) {
        this.host = host;
        this.instant = instant;
        this.openConnexion = openConnexion;
        this.currentLoad = currentLoad;
        this.maxLoad = maxLoad;
    }

    public String getHost() {
        return host;
    }

    public Instant getInstant() {
        return instant;
    }

    public int getOpenConnexion() {
        return openConnexion;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    @Override
    public String toString() {
        return "CHostMonit{" +
                "host='" + host + '\'' +
                ", instant=" + instant +
                ", openConnexion=" + openConnexion +
                ", currentLoad=" + currentLoad +
                ", maxLoad=" + maxLoad +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CHostMonit that = (CHostMonit) o;
        return openConnexion == that.openConnexion && currentLoad == that.currentLoad && maxLoad == that.maxLoad && Objects.equals(host, that.host) && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, instant, openConnexion, currentLoad, maxLoad);
    }
}
