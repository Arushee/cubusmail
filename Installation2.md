Cubusmail is relatively easy to install. All you need is at least JDK 5.0 and a servlet container like Tomcat or Jetty. No external database is necessary. Cubusmail supports IMAP and SMTP with or without SSL.


Steps to deploy Cubusmail to a servlet container.

1. Unpack the cubusmail.zip/tar.gz file.

2. Create a data directory under which persistent files used by Cubusmail are to be stored.
For example I use `/var/data/cubusmail` on Linux.

3. Copy the file `cubus.properties` to any directory you want. It can be the same as described above. For example, I use `/etc/cubusmail` on Linux.

4. Edit the `cubus.properties` and modify the following properties to match your invironment

Example
```
cubus.mail.imapSSL=true
cubus.mail.imapHost=myserver.com
cubus.mail.imapPort=993
cubus.mail.smtpSSL=false
cubus.mail.smtpHost=myserver.com
cubus.mail.smtpPort=25
cubus.mail.domainName=myserver.com
```

4. Modify the property `cubus.db.jdbcUrl` an change the path to the data directory.

Example
```
cubus.db.jdbcUrl=jdbc:h2:///var/data/cubusmail
```

5. Configure a JVM system property named `cubus.config` for the container. For Tomcat for example, add a line similar to the following to `$CATALINA_BASE/bin/catalina.sh`
```
JAVA_OPTS="$JAVA_OPTS -Dcubus.config=/etc/cubusmail/cubus.properties"
```

6. Delploy the cubusmail.war file to the container.

7. Run the container and start the application with `http://myserver.com:8080/cubusmail/`