package cz.cvut.fel.pjv.simulation.network.client;

/**
 * Instance of this is what gets created in network communication
 * this has uuid, reuquest and response needed for network communication.
 */
public class Request {
    String request;
    String uuid;
    String response;

    public Request(String request, String uuid) {
        this.request = request;
        this.uuid = uuid;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
