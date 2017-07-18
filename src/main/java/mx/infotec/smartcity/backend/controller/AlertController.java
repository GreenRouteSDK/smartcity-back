package mx.infotec.smartcity.backend.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mx.infotec.smartcity.backend.model.Alert;
import mx.infotec.smartcity.backend.model.Data;
import mx.infotec.smartcity.backend.model.Notification;
import mx.infotec.smartcity.backend.model.UserProfile;
import mx.infotec.smartcity.backend.persistence.AlertRepository;
import mx.infotec.smartcity.backend.persistence.UserProfileRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import mx.infotec.smartcity.backend.model.OrionAlert;

/**
 *
 * @author Adrian Molina
 */
@RestController
@RequestMapping("/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    // @RequestMapping(method = RequestMethod.GET)
    // public List<Alert> getAll() {
    // List<Alert> res = alertRepository.findAllByOrderByDateTimeDesc();
    //
    // if (res == null) {
    // return new ArrayList<>();
    // } else {
    // return res;
    // }
    // }

    @RequestMapping(method = RequestMethod.GET, value = "/page/{page}/items/{size}")
    public Page<Alert> getByPageSize(@PathVariable("page") String page, @PathVariable("size") String size) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        return alertRepository.findAllByOrderByDateTimeDesc(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/date/{date}/page/{page}/items/{size}")
    public Page<Alert> getByDate(@DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") Date date,
            @PathVariable("page") String page, @PathVariable("size") String size) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        return alertRepository.findByDateTimeBetweenOrderByDateTimeDesc(date, c.getTime(), pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Alert getById(@PathVariable int id) {
        return alertRepository.findOne(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}/page/{page}/items/{size}")
    public Page<Alert> getAllByUser(@PathVariable("id") String id, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        UserProfile profile = userProfileRepository.findOne(id);
        List<String> alertTypes = new ArrayList<>();
        profile.getGroups().forEach((group) -> {
            alertTypes.addAll(group.getNotificationIds());
        });

//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        Date today = new Date();
//        try {
//            today = formatter.parse(formatter.format(new Date()));
//        } catch (ParseException ex) {
//            Logger.getLogger(AlertController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(today);
//        c.add(Calendar.DATE, 1);
//        c.add(Calendar.SECOND, -1);
//
//        Page<Alert> res = alertRepository.findByAlertTypeInAndDateTimeBetweenOrderByDateTimeDesc(alertTypes, today,
//                c.getTime(), pageable);

          Page<Alert> res = alertRepository.findByAlertTypeInOrderByDateTimeDesc(alertTypes, pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/type/{alertType}/page/{page}/items/{size}")
    public Page<Alert> getAllByAlert(@PathVariable("alertType") String alertType, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Page<Alert> res = alertRepository.findByAlertTypeOrderByDateTimeDesc(alertType, pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/type/{alertType}/subtype/{subtype}/page/{page}/items/{size}")
    public Page<Alert> getAllByAlertAndSubalert(@PathVariable("alertType") String alertType,
            @PathVariable("subtype") String subtype, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Page<Alert> res = alertRepository.findByAlertTypeAndEventObservedOrderByDateTimeDesc(alertType, subtype,
                pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/type/{alertType}/date/{date}/page/{page}/items/{size}")
    public Page<Alert> getAllByAlertAndDate(@PathVariable("alertType") String alertType,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") Date date, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        Page<Alert> res = alertRepository.findByAlertTypeAndDateTimeBetweenOrderByDateTimeDesc(alertType, date,
                c.getTime(), pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/type/{alertType}/subtype/{subtype}/date/{date}/page/{page}/items/{size}")
    public Page<Alert> getAllByAlertAndSubalertAndDate(@PathVariable("alertType") String alertType,
            @PathVariable("subtype") String subtype,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") Date date, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        Page<Alert> res = alertRepository.findByAlertTypeAndEventObservedAndDateTimeBetweenOrderByDateTimeDesc(
                alertType, subtype, date, c.getTime(), pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/my-events/{id}/page/{page}/items/{size}")
    public Page<Alert> getAllEventsByUser(@PathVariable("id") String id, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Page<Alert> res = alertRepository.findByRefUserOrderByDateTimeDesc(id, pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/my-events/{id}/type/{alertType}/page/{page}/items/{size}")
    public Page<Alert> getAllEventsByUserAndAlertType(@PathVariable("id") String id,
            @PathVariable("alertType") String alertType, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Page<Alert> res = alertRepository.findByRefUserAndAlertTypeOrderByDateTimeDesc(id, alertType, pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/my-events/{id}/type/{alertType}/subtype/{subtype}/page/{page}/items/{size}")
    public Page<Alert> getAllEventsByUserAndAlertTypeAndSubtype(@PathVariable("id") String id,
            @PathVariable("alertType") String alertType, @PathVariable("subtype") String subtype,
            @PathVariable("page") String page, @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Page<Alert> res = alertRepository.findByRefUserAndAlertTypeAndEventObservedOrderByDateTimeDesc(id, alertType,
                subtype, pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/my-events/{id}/date/{date}/page/{page}/items/{size}")
    public Page<Alert> getAllEventsByUserAndDate(@PathVariable("id") String id,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") Date date, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        Page<Alert> res = alertRepository.findByRefUserAndDateTimeBetweenOrderByDateTimeDesc(id, date, c.getTime(),
                pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/my-events/{id}/type/{alertType}/date/{date}/page/{page}/items/{size}")
    public Page<Alert> getAllEventsByUserAndAlertTypeAndDate(@PathVariable("id") String id,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") Date date,
            @PathVariable("alertType") String alertType, @PathVariable("page") String page,
            @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        Page<Alert> res = alertRepository.findByRefUserAndAlertTypeAndDateTimeBetweenOrderByDateTimeDesc(id, alertType,
                date, c.getTime(), pageable);
        return res;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/my-events/{id}/type/{alertType}/subtype/{subtype}/date/{date}/page/{page}/items/{size}")
    public Page<Alert> getAllEventsByUserAndAlertTypeAndSubtypeAndDate(@PathVariable("id") String id,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @PathVariable("date") Date date,
            @PathVariable("alertType") String alertType, @PathVariable("subtype") String subtype,
            @PathVariable("page") String page, @PathVariable("size") String size, HttpServletRequest request) {
        Pageable pageable = new PageRequest(Integer.parseInt(page), Integer.parseInt(size));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        c.add(Calendar.SECOND, -1);
        Page<Alert> res = alertRepository
                .findByRefUserAndAlertTypeAndEventObservedAndDateTimeBetweenOrderByDateTimeDesc(id, alertType, subtype,
                        date, c.getTime(), pageable);
        return res;
    }
    
        @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Void> createNotification(@RequestBody OrionAlert orionAlert, UriComponentsBuilder ucBuilder) {
        /* Se obtienen las entidades de la clave principal determinada por el campo DATA */
        List<Data> datos = orionAlert.getData();
       
        Map <String, String> eventsObserved = new HashMap <>();
        eventsObserved.put("precipitation", "WeatherConditions");
        eventsObserved.put("relativeHumidity", "WeatherConditions");
        eventsObserved.put("temperature", "WeatherConditions");
        eventsObserved.put("windDirection", "WeatherConditions");
        eventsObserved.put("windSpeed", "WeatherConditions");
        eventsObserved.put("CO","Pollution");
        eventsObserved.put("NO2","Pollution");
        eventsObserved.put("NOx","Pollution");
        eventsObserved.put("SO2","Pollution");
        eventsObserved.put("O3","Pollution");
        eventsObserved.put("PM10","Pollution");
        
        for(Data data : datos){
            if(data.getType().equals("AirQualityObserved")){
                for(Map.Entry<String, String> entry : eventsObserved.entrySet() ){
                    Alert alert = new Alert(data, entry.getKey(), entry.getValue());
                    if(alert.getFound())
                        alertRepository.save(alert);
                }
            }else if(data.getType().equals("Alert")){
                alertRepository.save(new Alert(data, "", ""));
            }
        }
        
        
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
        
}
