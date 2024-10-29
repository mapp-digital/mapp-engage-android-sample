package com.appoxee.testapp;

public class GdprAgreement {
    private boolean shown;
    private boolean accepted;

    public GdprAgreement() {
        this(false, false);
    }

    public GdprAgreement(boolean shown, boolean accepted) {
        this.shown = shown;
        this.accepted = accepted;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
