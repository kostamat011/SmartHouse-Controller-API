/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geography;

import java.io.Serializable;

/**
 *
 * @author Kosta
 */
public class Position implements Serializable {

    private double lat;
    private double lng;

    public Position(double lattitude, double longitude) {
        this.lat = lattitude;
        this.lng = longitude;
    }
    
     public Position() {
    
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
    
    

    

}
