package business;

import java.util.*;
import components.data.*;
import service.*;

public class BusinessLayer
{
   DBSingleton db;
   
   /**
    * Starts up the database or resets it if already loaded
   */
   public String initialize()
   {
      db = DBSingleton.getInstance();
      db.db.initialLoad("LAMS");
      return "Database Initialized";
   }
   
   /**
    * Gets every appointment
    * @return every appointment in the database as a list
   */
   public List<Appointment> getAllAppointments()
   {
      List<Object> objs = db.db.getData("Appointment", "");
      List<Appointment> appts = new ArrayList<Appointment>();
      
      if( objs.isEmpty() )
      {
         this.initialize();
         objs = db.db.getData("Appointment", "");
      }
      
      for( Object obj : objs )
      {
         appts.add((Appointment)obj);
      }
      
      return appts;
   }
   
   /**
    * Gets an appointment from the database
    * @param apptNum - the appointment number to return the object of
    * @return a specific appointment
   */
   public Appointment getAppointment(String apptNum)
   {
      if(db == null)
      {
         this.initialize();
      }
   
      try
      {
         List<Object> objs = db.db.getData("Appointment", "id='" + apptNum + "'");
         return (Appointment)objs.get(0);
      }
      catch(Exception e)
      {
         return null;
      }
   }
   
   /**
    * Add an appointment given an XML string
    * @param id         - the appointment id to create
    * @param date       - the date the appointment is on
    * @param time       - the time the appointment is at
    * @param patientId  - the id of the patient the appointment is for
    * @param phlebId    - the id of the phlebotomist the appointment is with
    * @param pscId      - the id of the PSC where the appointment is at
    * @param tests      - a list of all the test id's and dxcodes for the appointment
    * @return either a list of all of the appointments in the database or an error string
   */
   public Object addAppointment(String id, java.sql.Date date, 
      java.sql.Time time, int patientId, int phlebId, 
      int pscId, HashMap<Integer, Double> tests)
   {
      Appointment newAppt = new Appointment("800", date, time);
      
      //grab info from database
      Patient pat = (Patient)db.db.getData("Patient", "id='" + patientId + "'").get(0);
      Phlebotomist phleb = (Phlebotomist)db.db.getData("Phlebotomist", "id='" + phlebId + "'").get(0);
      PSC psc = (PSC)db.db.getData("PSC", "id='" + pscId + "'").get(0);
      
      //create tests from the list
      List<AppointmentLabTest> testCol = new ArrayList<AppointmentLabTest>();
      
      for( Integer key : tests.keySet() )
      {
         Double testDx = tests.get(key);
         
         AppointmentLabTest test = new AppointmentLabTest(id, key.toString(), testDx.toString());
         Diagnosis dx = (Diagnosis)db.db.getData("Diagnosis", "code='" + testDx + "'").get(0);
         LabTest labTest = (LabTest)db.db.getData("LabTest", "id='" + key + "'").get(0);
         
         test.setDiagnosis(dx);
         test.setLabTest(labTest);
         
         testCol.add(test);
      }
      
      newAppt.setAppointmentLabTestCollection(testCol);
      newAppt.setPatientid(pat);
      newAppt.setPhlebid(phleb);
      newAppt.setPscid(psc);

      boolean good = db.db.addData(newAppt);
      List<Object> objs = db.db.getData("Appointment", "");
      
      if( good )
      {
         return objs;
      }
      else
      {
         return "Error: Invalid appointment.";
      }
   }
   
   /**
    * Turns an appointment into XML
    * @param appt - the appointment to get XML for
    * @return the XML for the given appointment
   */
   public String ApptToXML(Appointment appt)
   {
      java.sql.Date date = appt.getApptdate();
      java.sql.Time time = appt.getAppttime();
      List<AppointmentLabTest> tests = appt.getAppointmentLabTestCollection();
      int id = Integer.parseInt(appt.getId());
      Patient pat = appt.getPatientid();
      Phlebotomist phleb = appt.getPhlebid();
      PSC psc = appt.getPscid();
      
      String xml = "<appointment ";
      xml += "date='" + date.toString() + "' ";
      xml += "id='" + id + "' ";
      xml += "time='" + time.toString() + "'>\n";
      
      xml += "<patient id='" + pat.getId() + "'>";
      xml += "<name>" + pat.getName() + "</name>";
      xml += "<address>" + pat.getAddress() + "</address>";
      xml += "<insurance>" + pat.getInsurance() + "</insurance>";
      xml += "<dob>" + pat.getDateofbirth() + "</dob></patient>\n";
      
      xml += "<phlebotomist id='" + phleb.getId() + "'>";
      xml += "<name>" + phleb.getName() + "</name></phlebotomist>\n";
      
      xml += "<psc id='" + psc.getId() + "'>";
      xml += "<name>" + psc.getName() + "</name></psc>\n";
      
      xml += "<allLabTests>";
      
      for( int i = 0; i < tests.size(); i++ )
      {
         AppointmentLabTest test = tests.get(i);
         
         xml += "<appointmentLabTest appointmentId='" + id + "' ";
         xml += "dxcode='" + test.getDiagnosis().getCode() + "' ";
         xml += "labTestId='" + test.getLabTest().getId() + "'/>\n";
      }
      
      xml += "</allLabTests></appointment>\n";
      
      return xml;
   }
}