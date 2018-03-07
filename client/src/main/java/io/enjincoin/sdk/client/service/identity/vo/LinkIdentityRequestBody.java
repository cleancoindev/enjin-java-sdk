package io.enjincoin.sdk.client.service.identity.vo;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

/**
 * <p>Link Identity Request class.
 * </p>
 */
public class LinkIdentityRequestBody {

    @SerializedName("ethereum_address")
    private Optional<String> ethereumAddress;

    /**
     * Class constructor
     * @param ethereumAddress
     */
    public LinkIdentityRequestBody(String ethereumAddress) {
        this.ethereumAddress = Optional.of(ethereumAddress);
    }

    /**
     * @return the ethereumAddress
     */
    public Optional<String> getEthereumAddress() {
        return ethereumAddress;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LinkIdentityRequestVO [ethereumAddress=" + ethereumAddress + "]";
    }


}