package com.xcentral.xcentralback.utils;




public record ChangePassword(String password, String confirmPassword, int otp) {

    public int getOtp() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOtp'");
    }
    
}
