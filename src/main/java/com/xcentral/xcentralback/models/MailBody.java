package com.xcentral.xcentralback.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MailBody {
    private String to;
    private String from;
    private String subject;
    private String text;
    private String token;
}
