# Download yout Blobstore
Download the contents of the GAE blobstore to local disk with help of the [Remote API](https://cloud.google.com/appengine/docs/java/tools/remoteapi). 

## Set up Remote API support for your current GAE project
Add this servlet and servetmapping to your current web.xml en deploy your app.

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
- SERVER_STRING: [your_app_id].appspot.com. Can also be any sub version.
- OUTPUT_FOLDER: this is where the blobs are downloaded. default is `_ouput`.

## Whats next?
This is a very basic example. It's only meant for downloading from the Blobstore and doesn't support restoring for example (because the Blobstore is deprecated).
Next you could:
- Download the blobs to [Google Cloud Storage](https://cloud.google.com/storage/) instead of local disk.
- Check if a [blob](https://cloud.google.com/appengine/docs/java/javadoc/com/google/appengine/api/blobstore/BlobInfo) from the Blobstore is newer than the local version before you skip it.
- Introduce chunking for `queryBlobInfos`.

