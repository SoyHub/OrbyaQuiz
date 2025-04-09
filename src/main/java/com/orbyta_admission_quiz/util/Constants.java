package com.orbyta_admission_quiz.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String API_BASE_PATH = "/api/v1";
    public static final String ACCOUNT_PATH = "/accounts";

    public static final String AUTH_SCHEMA_HEADER = "Auth-Schema";
    public static final String API_KEY_HEADER = "Api-Key";
    public static final String X_TIME_ZONE_HEADER = "X-Time-Zone";

    public static final String EUROPE_ROME_TIME_ZONE_VALUE = "Europe/Rome";

    public static final String ACCOUNT_ID_FIELD_KEY = "accountId";

}
