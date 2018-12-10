# Fun-Flowers

This repo contains the pom.xml and a Flexion.java Class that implements the Integration interface. It is in java 8. This was developed as a Netbeans Maven project.
It sends post and get requests to a remote server.<br> 
(get json objects url: http://sandbox.flexionmobile.com/javachallenge/rest/developer/ioanniskoutoulakis/all/)
The code passes the integration tests.<br>
NOTE1: there have been reports that importing a Netbeans project to Eclipse might be buggy.<br>
NOTE2: I've used jackson with MrBeanModule so that I can map the response json to the Purchase interface without having to create and implement this as well (as the objective stated to implement only the Integration interface)
