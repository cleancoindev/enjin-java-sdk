package com.enjin.enjincoin.sdk.client.service.requests.vo;

import com.google.gson.annotations.SerializedName;

/**
 * <p>Request Response class.</p>
 */
public class UpdateRequestResponseBody {

    @SerializedName("id")
    private Integer id;

    @SerializedName("transaction_id")
    private Integer transactionId;

    @SerializedName("app_id")
    private Integer appId;

    @SerializedName("type")
    private String type;

    @SerializedName("value")
    private String value;

    @SerializedName("accepted")
    private Integer accepted;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_at")
    private String createdAt;


    /**
     * Class constructor
     * @param id
     * @param transactionId
     * @param appId
     * @param type
     * @param value
     * @param accepted
     * @param updatedAt
     * @param createdAt
     */
    public UpdateRequestResponseBody(Integer id, Integer transactionId, Integer appId, String type, String value,
            Integer accepted, String updatedAt, String createdAt) {
        super();
        this.id = id;
        this.transactionId = transactionId;
        this.appId = appId;
        this.type = type;
        this.value = value;
        this.accepted = accepted;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * @return the transactionId
     */
    public Integer getTransactionId() {
        return this.transactionId;
    }

    /**
     * @return the appId
     */
    public Integer getAppId() {
        return this.appId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @return the accepted
     */
    public Integer getAccepted() {
        return this.accepted;
    }

    /**
     * @return the updatedAt
     */
    public String getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * @return the createdAt
     */
    public String getCreatedAt() {
        return this.createdAt;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UpdateRequestResponseBody [id=" + this.id + ", transactionId=" + this.transactionId + ", appId=" + this.appId + ", type=" + this.type + ", value=" + this.value + ", accepted=" + this.accepted
                + ", updatedAt=" + this.updatedAt + ", createdAt=" + this.createdAt + "]";
    }
}

