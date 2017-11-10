package service;

import java.util.*;
import components.data.*;
import business.*;

public class LAMSService
{
   BusinessLayer business;
   
   public LAMSService()
   {
      business = new BusinessLayer();
   }

   /**
    * Starts up the database or resets it if already loaded
   */
   public String initialize()
   {
      return business.initialize();
   }
   
   /**
    * Gets every appointment as XML
    * @return every appointment in the database as XML
   */
   public String getAllAppointments()
   {
      List<Appointment> objs = business.getAllAppointments();
      String result = "<?xml version='1.0' encoding='UTF-8' standalone='no'?> <AppointmentList> ";
      
      for (Object obj : objs){
          result += business.ApptToXML((Appointment)obj) + "\n";
      }
      
      result += "</AppointmentList>";
      
      return result;
   }
   
   /**
    * Gets an appointment as XML
    * @param apptNum - the appointment number to return the XML of
    * @return a specific appointment as XML
   */
   public String getAppointment(String apptNum)
   {
      Object obj = business.getAppointment(apptNum);
      
      String msg = (obj == null ? "<error>Invalid appointment number</error>" : business.ApptToXML((Appointment)obj) );
      
      String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + 
          "<AppointmentList>";
      
      xml += msg;
      xml += "</AppointmentList>";

      return xml;
   }
   
   /**
    * Add an appointment given an XML string
    * @param xml - the XML string of the appointment to add
    * @return either all of the appointments in the database as XML or an error string as XML
   */
   public String addAppointment(String xml)
   {
      //pull data from XML to pass to business layer
      int patient = 220;
      int phleb = 110;
      int psc = 510;
      
      //make a nice calendar so that java.sql.Date will play nice
      Calendar goodDate = Calendar.getInstance();
      goodDate.set(2009, 9, 1);
      
      java.sql.Date date = new java.sql.Date(goodDate.getTime().getTime());
      java.sql.Time time = new java.sql.Time(10, 15, 0);
      
      //extra steps here due to persistence api and join, need to create objects in list
      HashMap<Integer, Double> tests = new HashMap<Integer, Double>();
      tests.put(86900,292.9);

      Object good = business.addAppointment( "800", date, time, patient, phleb, psc, tests );
      
      if( good instanceof List<?> )
      {   
         return this.getAllAppointments();
      }
      else //error
      {
         return (String)good;
      }
   }
}