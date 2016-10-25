# Download your Blobstore
Download the contents of the GAE blobstore to local disk with help 
of the [Remote API](https://cloud.google.com/appengine/docs/java/tools/remoteapi). 

## Set up Remote API support for your current GAE project
Add this servlet and servletmapping to your current web.xml en deploy your app.

```
<servlet>
  <display-name>Remote API Servlet</display-name>
  <servlet-name>RemoteApiServlet</servlet-name>
  <servlet-class>com.google.apphosting.utils.remoteapi.RemoteApiServlet</servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
  <servlet-name>RemoteApiServlet</servlet-name>
  <url-pattern>/remote_api</url-pattern>
</servlet-mapping>
```

The Remote API client will rely on Application Default Credentials that use OAuth 2.0.
In order to get a credential run: `gcloud init`

## App Settings
Copy the file `settings.properties.example` to `settings.properties` and adjust
the following settings inside it:
- `appId`: [your_app_id].appspot.com. Can also be any sub version.
- `outputFolder`: this is where the blobs are downloaded.

## Running `App` class
Either run `App` from your favorite IDE, or, if Maven is installed locally, do:

```
mvn exec:java
```

## Whats next?
This is a very basic example. It's only meant for downloading from the Blobstore 
and doesn't support restoring for example (because the Blobstore is deprecated).
Next you could:
- Download the blobs to [Google Cloud Storage](https://cloud.google.com/storage/) 
  instead of local disk.
- Check if a [blob](https://cloud.google.com/appengine/docs/java/javadoc/com/google/appengine/api/blobstore/BlobInfo) 
  from the Blobstore is newer than the local version before you skip it.
- Introduce chunking for `queryBlobInfos`.

## License
Download your Blobstore is available under the MIT license, so feel free to use it in commercial and non-commercial projects. See the license file for more info.
