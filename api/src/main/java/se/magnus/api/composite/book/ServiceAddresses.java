package se.magnus.api.composite.book;

public class ServiceAddresses {
    private final String cmp;
    private final String boo;
    private final String rat;
    private final String com;
    private final String bthn;

    public ServiceAddresses() {
        cmp = null;
        boo = null;
        rat = null;
        com = null;
        bthn = null;
    }

    public ServiceAddresses(String compositeAddress, String bookAddress, String ratingAddress, String commentAddress, String bookThemeNightAddress) {
        this.cmp = compositeAddress;
        this.boo = bookAddress;
        this.rat = ratingAddress;
        this.com = commentAddress;
        this.bthn = bookThemeNightAddress;
    }

    public String getCmp() {
        return cmp;
    }

    public String getBoo() {
        return boo;
    }

    public String getRat() {
        return rat;
    }

    public String getCom() {
        return com;
    }
    

    public String getBthn() {
        return bthn;
    }
}
