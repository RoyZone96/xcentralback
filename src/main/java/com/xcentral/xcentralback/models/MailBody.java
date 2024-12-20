package com.xcentral.xcentralback.models;

import lombok.Builder;

@Builder
public record MailBody(String to, String from, String subject, String text, String token) {

    
}
