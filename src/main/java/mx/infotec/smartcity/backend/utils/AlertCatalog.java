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
            return ("Temperature: " + valor + " °C <br> Hyper-glacial: thermal very cold and extreme cold stress.");
        }
        else if(temperature>=-39&&temperature<=-20){
            return ("Temperature: " + valor + " °C <br> Glacial: thermal sensitivity cold and strong cold stress.");
        }
        else if(temperature>=-19&&temperature<=-10){
            return ("Temperature: " + valor + " °C <br> Extremely cold: thermal sensitivity cool and moderate cold stress.");
        }
        else if(temperature>=-9&&temperature<=-1){
            return ("Temperature: " + valor + " °C <br> Very cold: thermal sensitivity slightly cool and slight cold stress.");
        }
        else if(temperature>=0&&temperature<=13){
            return ("Temperature: " + valor + " °C <br> Cold: thermal sensitivity comfortable and no thermal stress.");
        }
        else if(temperature>=27&&temperature<=32){
            return ("Temperature: " + valor + " °C <br> Caution: Fatigue is possible with prolonged exposure and activity. Continuing activity could result in heat cramps.");
        }
        else if(temperature>=33&&temperature<=41){
            return ("Temperature: " + valor + " °C <br> Alert: Heat cramps and heat exhaustion are possible. Continuing activity could result in heat stroke.");
        }
        else if(temperature>=42&&temperature<=54){
            return ("Temperature: " + valor + " °C <br> Warning: Heat cramps and heat exhaustion are likely; heat stroke is probable with continued activity.");
        }
        else if(temperature>54){
            return("Temperature: " + valor + " °C <br> Danger: Extreme danger: heat stroke is imminent.");
        }
          return ("Temperature: " + valor + " °C");  
    }
    
    public static String seteventObservedTemperature(String valor){
        Float temperature = Float.parseFloat(valor);
        if(temperature<=-10){
            return("Cold alert");
        }
        else if(temperature>=-9&&temperature<=32){
            return("Temperature");
        }
        else if(temperature>=33){
            return("Heat alert");
        }
        
        return valor;
    }
    
    public static String setAlertHumidity(String valor){
        Float humidity = Float.parseFloat(valor);
        if(humidity<=29){
            return("Relative humidity: "+ valor + " %. <br> No discomfort.");
        }else if(humidity>=30&&humidity<=39){
            return("Relative humidity: "+ valor + " %. <br> Some discomfort.");
        }else if(humidity>=40&&humidity<=45){
            return("Relative humidity: "+ valor + " %. <br> Great discomfort: avoid exertion.");
        }else if(humidity>=46&&humidity<=54){
            return("Relative humidity: "+ valor + " %. <br> Dangerous.");
        }else if(humidity>54){
            return("Relative humidity: "+ valor + " %. <br> Heat stroke imminent.");
        }
        
        return ("Relative humidity: " + valor);
    }
    
    public static String AlertPollution(String valor, String pollutant){
        Float pollution = Float.parseFloat(valor);
        
        switch(pollutant){
            case "PM10":
                if(pollution>=0&&pollution<=50){
                    return ("Particulate matter: " + valor + " GQ <br> Good: Air quality is considered satisfactory, and air pollution poses little or no risk.");
                }else if(pollution>=51&&pollution<=100){
                    return ("Particulate matter: " + valor + " GQ <br> Acceptable: Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.");
                }else if(pollution>=101&&pollution<=250){
                    return ("Particulate matter: " + valor + " GQ <br> Moderately polluted: Children, older adults, people who engage in intense physical activity or with respiratory and cardiovascular diseases, should limit prolonged outdoor efforts.");
                }else if(pollution>=251&&pollution<=350){
                    return ("Particulate matter: " + valor + " GQ <br> Damage to health: Everyone can experience health effects; Who belong to sensitive groups may experience serious health effects.");
                }else if(pollution>=351&&pollution<=430)
                    return ("Particulate matter: " + valor + " GQ <br> Very danger: It represents an emergency condition. The entire population is likely to be affected.");
                break;
            case "NO2":
                if(pollution>=0&&pollution<=40){
                    return ("Nitrogen dioxide: " + valor + " PPB <br> Good: Air quality is considered satisfactory, and air pollution poses little or no risk.");
                }else if(pollution>=41&&pollution<=80){
                    return ("Nitrogen dioxide: " + valor + " PPB <br> Acceptable: Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.");
                }else if(pollution>=81&&pollution<=180){
                    return ("Nitrogen dioxide: " + valor + " PPB <br> Moderately polluted: Children, older adults, people who engage in intense physical activity or with respiratory and cardiovascular diseases, should limit prolonged outdoor efforts.");
                }else if(pollution>=181&&pollution<=280){
                    return ("Nitrogen dioxide: " + valor + " PPB <br> Damage to health: Everyone can experience health effects; Who belong to sensitive groups may experience serious health effects.");
                }else if(pollution>=281&&pollution<=400){
                    return ("Nitrogen dioxide: " + valor + " PPB <br> Very danger: It represents an emergency condition. The entire population is likely to be affected.");
                }
                break;
            case "O3":
                if(pollution>=0&&pollution<=50){
                    return ("Ozone: " + valor + " PPB <br> Good: Air quality is considered satisfactory, and air pollution poses little or no risk.");
                }else if(pollution>=51&&pollution<=100){
                    return ("Ozone: " + valor + " PPB <br> Acceptable: Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.");
                }else if(pollution>=101&&pollution<=168){
                    return ("Ozone: " + valor + " PPB <br> Moderately polluted: Children, older adults, people who engage in intense physical activity or with respiratory and cardiovascular diseases, should limit prolonged outdoor efforts.");
                }else if(pollution>=169&&pollution<=208){
                    return ("Ozone: " + valor + " PPB <br> Damage to health: Everyone can experience health effects; Who belong to sensitive groups may experience serious health effects.");
                }else if(pollution>=209&&pollution<=748){
                    return("Ozone: " + valor + " PPB <br> Very danger: It represents an emergency condition. The entire population is likely to be affected.");
                }
                break;
            case "CO":
                if(pollution>=0&&pollution<=40){
                    return ("Carbon monoxide: " + valor + " PPM <br> Good: Air quality is considered satisfactory, and air pollution poses little or no risk.");
                }else if(pollution>=41&&pollution<=80){
                    return ("Carbon monoxide: " + valor + " PPM <br> Acceptable: Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.");
                }else if(pollution>=81&&pollution<=380){
                    return ("Carbon monoxide: " + valor + " PPM <br> Moderately polluted: Children, older adults, people who engage in intense physical activity or with respiratory and cardiovascular diseases, should limit prolonged outdoor efforts.");
                }else if(pollution>=381&&pollution<=800){
                    return ("Carbon monoxide: " + valor + " PPM <br> Damage to health: Everyone can experience health effects; Who belong to sensitive groups may experience serious health effects.");
                }else if(pollution>=801&&pollution<=1600){
                    return ("Carbon monoxide: " + valor + " PPM <br> Very danger: It represents an emergency condition. The entire population is likely to be affected.");
                }
                break;
            case "SO2":
                if(pollution>=0&&pollution<=40){
                    return ("Sulfur monoxide: " + valor + " PPB <br> Good: Air quality is considered satisfactory, and air pollution poses little or no risk.");
                }else if(pollution>=41&&pollution<=80){
                    return ("Sulfur monoxide: " + valor + " PPB <br> Acceptable: Air quality is acceptable; however, for some pollutants there may be a moderate health concern for a very small number of people who are unusually sensitive to air pollution.");
                }else if(pollution>=81&&pollution<=380){
                    return ("Sulfur monoxide: " + valor + " PPB <br> Moderately polluted: Children, older adults, people who engage in intense physical activity or with respiratory and cardiovascular diseases, should limit prolonged outdoor efforts.");
                }else if(pollution>=381&&pollution<=800){
                    return ("Sulfur monoxide: " + valor + " PPB <br> Damage to health: Everyone can experience health effects; Who belong to sensitive groups may experience serious health effects.");
                }else if(pollution>=801&&pollution<=1600){
                    return("Sulfur monoxide: " + valor + " PPB <br> Very danger: It represents an emergency condition. The entire population is likely to be affected.");
                }                
                break;
        }
        
        return valor;
    }
        
    /*public static void main(String [] args){
            AlertCatalog prueba = new AlertCatalog();
            System.out.println(prueba);
            System.out.println(prueba.setAlertTemperature("-40"));

    }*/   
   
}


