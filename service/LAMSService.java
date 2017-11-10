package service;

import java.util.*;
import components.data.*;
import business.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import java.io.*;

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
      try
      {
         DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = db.parse( new InputSource(new StringReader(xml)) );
         Element root = doc.getDocumentElement();
      
         String dateStr = business.getTextContent(doc, "date");
         String timeStr = business.getTextContent(doc, "time");
         //get appt id
      
         int year = Integer.parseInt( dateStr.split("-")[0] );
         int month = Integer.parseInt( dateStr.split("-")[1] );
         int day = Integer.parseInt( dateStr.split("-")[2] );
      
         int hour = Integer.parseInt( timeStr.split(":")[0] );
         int min = Integer.parseInt( timeStr.split(":")[1] );
   
         //pull data from XML to pass to business layer
         int patient = Integer.parseInt( business.getTextContent(doc, "patientId") );
         int phleb = Integer.parseInt( business.getTextContent(doc, "phlebotomistId") );
         int psc = Integer.parseInt( business.getTextContent(doc, "pscId") );
      
         //make a nice calendar so that java.sql.Date will play nice
         Calendar goodDate = Calendar.getInstance();
         goodDate.set(year, month - 1, day);
      
         java.sql.Date date = new java.sql.Date(goodDate.getTime().getTime());
         java.sql.Time time = new java.sql.Time(hour, min, 0);
      
         NodeList testNodes = doc.getElementsByTagName("test");
         HashMap<Integer, Double> tests = new HashMap<Integer, Double>();
      
         //add tests to the map
         for( int i = 0; i < testNodes.getLength(); i++ )
         {
            Element e = (Element)testNodes.item(i);
            int id = Integer.parseInt( e.getAttribute("id") );
            double code = Double.parseDouble( e.getAttribute("dxcode") );
      
            tests.put(id, code);
         }

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
      catch(Exception e)
      {
         return "Invalid XML input";
      }
   }
}