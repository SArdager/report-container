package kz.kdlolymp.termocontainers.service;

public class TemporaryPasswordGenerator {

    public String generateTemporaryPassword() {
        String password = "";
        String symbolsSet= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        int randomNumber;
        for(int i=0; i<8; i++){
            randomNumber = 72 + (int)(Math.random()*8000);
            int pos = randomNumber%72;
            password += symbolsSet.substring(pos, pos+1);
        }
        return password;
    }
}
