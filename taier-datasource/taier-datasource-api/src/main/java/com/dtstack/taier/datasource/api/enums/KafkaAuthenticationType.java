package com.dtstack.taier.datasource.api.enums;

public enum KafkaAuthenticationType {

    SASL_PLAINTEXT(0, "PLAIN"),
    SASL_SCRAM(1, "SCRAM-SHA-256"),
    SASL_SCRAM_512(2, "SCRAM-SHA-512");
    private final int type;

    private final String mechanism;

    public int getType() {
        return type;
    }

    public String getMechanism() {
        return mechanism;
    }

    KafkaAuthenticationType(int type, String mechanism) {
        this.type = type;
        this.mechanism = mechanism;
    }
}
