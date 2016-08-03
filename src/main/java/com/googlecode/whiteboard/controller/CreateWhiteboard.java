/*
 * @author  Oleg Varaksin (ovaraksin@googlemail.com)
 * $$Id: CreateWhiteboard.java 90 2011-09-27 15:24:29Z ovaraksin@gmail.com $$
 */

package com.googlecode.whiteboard.controller;

import com.googlecode.whiteboard.model.UserData;
import com.googlecode.whiteboard.model.Whiteboard;
import com.googlecode.whiteboard.utils.EmailUtil;
import com.googlecode.whiteboard.utils.FacesAccessor;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Managed bean for the login dialog if a new whiteboard is created.
 *
 * @author ova / last modified by $Author: ovaraksin@gmail.com $
 * @version $Revision: 90 $
 */
public class CreateWhiteboard implements Serializable
{

    //~ Static fields/initializers ---------------------------------------------

    private static final long serialVersionUID = 20110501L;

    //~ Instance fields --------------------------------------------------------

    private Whiteboard whiteboard;
    private WhiteboardsManager whiteboardsManager;
    private String pubSubTransport = "websocket";
    private List<SelectItem> pubSubTransports;

    //~ Methods ----------------------------------------------------------------

    @PostConstruct
    /**
     * Creates and initialize a new whiteboard. This method is called automatically by JSF facility.
     */
    protected void initialize() {
        // create an empty whiteboard and uuid container of the current whiteboard
        whiteboard = new Whiteboard();
    }

    public String getTitle() {
        return whiteboard.getTitle();
    }

    public void setTitle(String title) {
        whiteboard.setTitle(title);
    }
    
    public String getInviters() {
        return whiteboard.getInviters();
    }

    public void setInviters(String inviters) {
        whiteboard.setInviters(inviters);
    }

    public String getEmail()
    {
    	return whiteboard.getEmail();
    }
    public void setEmail(String email)
    {
    	whiteboard.setEmail(email);
    }
    public String getCreator() {
        return whiteboard.getCreator();
    }

    public void setCreator(String userName) {
        whiteboard.setCreator(userName);
    }

    public int getWidth() {
        return whiteboard.getWidth();
    }

    public void setWidth(int width) {
        whiteboard.setWidth(width);
    }

    public int getHeight() {
        return whiteboard.getHeight();
    }

    public void setHeight(int height) {
        whiteboard.setHeight(height);
    }

    public void setWhiteboardsManager(WhiteboardsManager whiteboardsManager) {
        this.whiteboardsManager = whiteboardsManager;
    }

    public String getPubSubTransport() {
        return pubSubTransport;
    }

    public void setPubSubTransport(String pubSubTransport) {
        this.pubSubTransport = pubSubTransport;
    }

    public String getWhiteboardId() {
        return whiteboard.getUuid();
    }

    public void setWhiteboardId(String whiteboardId) {
        this.whiteboard.setUuid(whiteboardId);
    }
    
    public void SplitEmailsInviters(String inviters)
    {
    	String[] emails=inviters.split(",");
    	for(int i=0;i<emails.length;i++)
    	{
    		System.out.println(emails[i]);
    	}
    	
    	EmailForInvits();
    }
    public void EmailForInvits()
    {
    	String from = "chaimaa.elachaoui@gmail.com"; 
    	String pass = "chaimaachaimaa";
    	            String to = "fatimazahra523@gmail.com"; 
    	            String subject = "Invitation";
    	            String body = "on vous invite a notre whiteboard";

    	            Properties props = System.getProperties();
    	            String host = "smtp.gmail.com";
    	            props.put("mail.smtp.starttls.enable", "true" );
    	            props.put("mail.smtp.host", host);
    	            props.put("mail.smtp.user", from);
    	            props.put("mail.smtp.password", pass);
    	            props.put("mail.smtp.port", "587");
    	            props.put("mail.smtp.auth", "true");

    	    Session session = Session.getDefaultInstance(props);
    	            MimeMessage message = new MimeMessage(session);
    	      try {
    	    message.setFrom(new InternetAddress(from));
    	    InternetAddress toAddress = new InternetAddress(to);
    	    message.addRecipient(Message.RecipientType.TO, toAddress);
    	    message.setSubject(subject);
    	                message.setText(body);
    	                Transport transport = session.getTransport("smtp");
    	                System.out.println("Message sent1");
    	                transport.connect(host, from, pass);
    	                System.out.println("Message sent2");
    	                transport.sendMessage(message, message.getAllRecipients());
    	                System.out.println("Message sent3");
    	                transport.close();

    	    }
    	            catch (Exception me) {
    	                me.printStackTrace();
    	                
    	            }
    }
    	            /*
    	
    	String smtpHostServer = "smtp.gmail.com";
    	String fromEmail = "fatimazahra523@gmail.com";
	    String emailID = "fatimazahra523@gmail.com";
	    
	    Properties props = System.getProperties();
        
        props.put("mail.smtp.starttls.enable", "true" );
	    props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", "587");


	    Session session = Session.getInstance(props, null);
	    
	    EmailUtil.sendEmail(session,fromEmail, emailID,"SimpleEmail Testing Subject", "SimpleEmail Testing Body");
    	   
    }*/

    /**
     * Creates a new whiteboard
     *
     * @return string outcome for navigation
     */
    public String create() {
        String senderId = UUID.randomUUID().toString();

        whiteboard.setCreationDate(new Date());
        whiteboard.addUserData(new UserData(senderId, getCreator()));
        whiteboard.setPubSubTransport(pubSubTransport);
        whiteboardsManager.addWhiteboard(whiteboard);

        WhiteboardIdentifiers wi = ((WhiteboardIdentifiers) FacesAccessor.getManagedBean("whiteboardIdentifiers"));
        wi.setSenderId(senderId);
        EmailForInvits();
        System.out.println("probleme1 !");
        wi.setWhiteboardId(whiteboard.getUuid());
 
        System.out.println("probleme2 !");
        return "pretty:wbWorkplace";
    }

    public List getTransports() {
        if (pubSubTransports == null) {
            pubSubTransports = new ArrayList<SelectItem>();
            pubSubTransports.add(new SelectItem("websocket", "WebSocket"));
            pubSubTransports.add(new SelectItem("long-polling", "Long-Polling"));
            pubSubTransports.add(new SelectItem("streaming", "Streaming"));
        }

        return pubSubTransports;
    }
}
