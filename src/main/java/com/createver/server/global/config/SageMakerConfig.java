package com.createver.server.global.config;

public class SageMakerConfig {

    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN = "Token ";
    public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
    public static final String VERSION_1 = "ddfc2b08d209f9fa8c1eca692712918bd449f695dabb4a958da31802a9570fe4";
    public static final String VERSION_2 = "7be0f12c54a8d033a0fbd14418c9af98962da9a86f5ff7811f9b3423a1f0b7d7";
    public static final String WEBHOOK_END_POINT = "https://api.createver.site/api/v1/image/avatar/webhook";
    public static final String WEBHOOK_END_POINT_MUSIC = "https://api.createver.site/api/v1/music/create/webhook";
    public static final String WEBHOOK_EVENT_START = "start";
    public static final String WEBHOOK_EVENT_COMPLETED = "completed";
    public static final String RESPONSE_KEY_ID = "id";

    private SageMakerConfig(){
        throw new UnsupportedOperationException();
    }
}
