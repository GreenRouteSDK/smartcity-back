/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.infotec.smartcity.backend.utils;


/**
 *
 * @author yolanda.baca
 */
public class AlertCatalog {
    
       
    public AlertCatalog(){
    }
    
    public static String setAlertTemperature(String valor){
        Float temperature = Float.parseFloat(valor);
        
        if(temperature<=-40){
            return (String.valueOf(valor) + " °C Hyper-glacial: thermal very cold and extreme cold stress");
        }
        else if(temperature>=-39.9&&temperature<=-20){
            return (valor + " °C Glacial: thermal sensitivity cold and strong cold stress");
        }
        else if(temperature>=-21&&temperature<=-10){
            return (valor + " °C Extremely cold: thermal sensitivity cool and moderate cold stress");
        }
        else if(temperature>=-9.9&&temperature<=-1.8){
            return (valor + " °C Very cold: thermal sensitivity slightly cool and slight cold stress.");
        }
        else if(temperature>=-1.9&&temperature<=12.9){
            return (valor + " °C Cold: thermal sensitivity comfortable and no thermal stress.");
        }
        else if(temperature>=27&&temperature<=32){
            return (valor + " °C Caution: Fatigue is possible with prolonged exposure and activity. Continuing activity could result in heat cramps.");
        }
        else if(temperature>=33&&temperature<=41){
            return (valor + " °C Alert: Heat cramps and heat exhaustion are possible. Continuing activity could result in heat stroke.");
        }
        else if(temperature>=42&&temperature<=54){
            return (valor + " °C Warning: Heat cramps and heat exhaustion are likely; heat stroke is probable with continued activity.");
        }
        else if(temperature>54){
            return(valor + " °C Danger: Extreme danger: heat stroke is imminent.");
        }
          return String.valueOf(valor);  
    }
    
    public static String seteventObservedTemperature(String valor){
        Float temperature = Float.parseFloat(valor);
        if(temperature<=-40&&temperature<=-10){
            return("Cold alert");
        }
        else if(temperature>=-9.9&&temperature<=32){
            return("Temperature");
        }
        else if(temperature>=33){
            return("Heat alert");
        }
        
        return valor;
    }
    
    /* public static void main(String [] args){
            AlertCatalog prueba = new AlertCatalog();
            System.out.println(prueba);
            System.out.println(prueba.setAlertTemperature("28"));

    }*/
    
    
    
}


