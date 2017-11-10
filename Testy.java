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
      
      //String xml = "";
      
      //System.out.println(serv.addAppointment(xml));
      System.out.println("------------------------");
      
      System.out.println(serv.getAllAppointments());
      System.out.println("------------------------");
      
      System.out.println(serv.getAppointment("710"));
      System.out.println("------------------------");
      
      System.out.println(serv.getAppointment("1"));
      System.out.println("------------------------");
      
      System.out.println(serv.addAppointment("710"));
      System.out.println("------------------------");
      
      System.out.println(serv.getAppointment("800"));
   }
}