import business.*;
import service.*;

class Testy
{
   public static void main(String args[])
   {
      LAMSService serv = new LAMSService();
      BusinessLayer bus = new BusinessLayer();
      
      bus.initialize();
      serv.initialize();
      
      String xml = "<?xml version='1.0' encoding='utf-8' standalone='no'?>" + 
         "<appointment>  <date>2018-12-30</date>  <time>10:00</time>" + 
         "<patientId>220</patientId>  <physicianId>20</physicianId> " + 
         "<pscId>520</pscId>  <phlebotomistId>110</phlebotomistId>  " + 
         "<labTests>    <test id='86900' dxcode='292.9' />    " + 
         "<test id='86609' dxcode='307.3' />  </labTests></appointment>";
      
      //System.out.println(serv.getAllAppointments());
      System.out.println("------------------------");
      
      //System.out.println(serv.getAppointment("710"));
      System.out.println("------------------------");
      
      //System.out.println(serv.getAppointment("1"));
      System.out.println("------------------------");
      
      System.out.println(serv.addAppointment(xml));
      System.out.println("------------------------");
      
      System.out.println(serv.getAppointment("800"));
   }
}