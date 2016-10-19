package org.fs.android.dictionary.backend.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.cmd.Query;

import org.fs.android.dictionary.backend.model.DeviceObject;
import org.fs.android.dictionary.backend.model.TranslateObject;
import org.fs.android.dictionary.backend.model.UserObject;

import static org.fs.android.dictionary.backend.util.ObjectifyHelper.objectify;

import java.util.Date;
import java.util.List;

/**
 * Created by Fatih on 29/11/14.
 */
@Api(name = "snapDictionaryApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.dictionary.android.fs.org", ownerName = "backend.dictionary.android.fs.org", packagePath = "api"))
public class SnapDictionaryEndpoint {

//    private DatastoreService mDatastore  = DatastoreServiceFactory.getDatastoreService();
//
//    @ApiMethod(name = "registerDevice")
//    public DeviceResponse registerDevice(Device device) {
//        DeviceResponse deviceResponse = new DeviceResponse();
//        if(device != null)
//        {
//            Key deviceKey = device.getKey();
//            if(deviceKey == null)
//            {
//                Query.Filter filterByUuid = new Query.FilterPredicate("android_id", Query.FilterOperator.EQUAL, device.getAndroidId());
//                Query q = new Query("Device").setFilter(filterByUuid);
//                PreparedQuery pq = mDatastore.prepare(q);
//
//                Iterator<Entity> it = pq.asIterator();
//                //if we already not registered this device, this thing will be called only once.
//                if(!it.hasNext()) {
//
//                    device.setGuild(UUID.randomUUID().toString());
//                    deviceResponse.setCode(200);
//                    deviceResponse.setErrorCode(Integer.MIN_VALUE);
//                    deviceResponse.setMessage(null);
//
//                    //parse data entity
//                    Entity deviceEntity = new Entity("Device");
//                    deviceEntity.setProperty("uuid", device.getGuild());
//                    deviceEntity.setProperty("android_id", device.getAndroidId());
//                    deviceEntity.setProperty("android_os", device.getAndroidOsVersion());
//                    deviceEntity.setProperty("product_name", device.getProductName());
//
//                    //add data
//                    mDatastore.put(deviceEntity);
//                    device.setKey(deviceEntity.getKey());
//
//                    deviceResponse.setData(device);
//                }
//            }
//        }
//        else
//        {
//            //device is null so error is 1
//            deviceResponse.setErrorCode(1);
//            deviceResponse.setCode(404);
//            deviceResponse.setMessage("device object is null.");
//        }
//        return deviceResponse;
//    }
//
//    @ApiMethod(name = "signWith")
//    public UserResponse signWith(User user, @Named("identifier") String androidId) {
//        UserResponse userResponse = new UserResponse();
//        if(user != null)
//        {
//            if(androidId != null)
//            {
//                //look for registered device for android id.
//                Query.Filter filterByUuid = new Query.FilterPredicate("android_id", Query.FilterOperator.EQUAL, androidId);
//                Query q = new Query("Device").setFilter(filterByUuid);
//                PreparedQuery pq = mDatastore.prepare(q);
//                //look for it
//                Iterator<Entity> it = pq.asIterator();
//                //if its found.
//                if(it.hasNext()) {
//                    //first one is ours always!
//                    Entity deviceEntity = it.next();
//
//                    Entity userEntity = new Entity("User", deviceEntity.getKey());
//                    userEntity.setProperty("name", user.getName());
//                    userEntity.setProperty("birthday", user.getBirtday());
//                    userEntity.setProperty("gender", user.getGender());
//                    userEntity.setProperty("about", user.getAbout());
//                    userEntity.setProperty("location", user.getLocaltion());
//
//                    mDatastore.put(userEntity);
//
//                    user.setKey(userEntity.getKey());
//
//                    userResponse.setCode(200);
//                    userResponse.setErrorCode(Integer.MIN_VALUE);
//                    userResponse.setMessage(null);
//                    userResponse.setData(user);
//                }
//                else
//                {
//                    userResponse.setCode(404);
//                    userResponse.setErrorCode(2);
//                    userResponse.setMessage("there is not device found for this id. Register your device first.");
//                    userResponse.setData(null);
//                }
//            }
//        }
//        //error
//        else
//        {
//            userResponse.setCode(404);
//            userResponse.setErrorCode(1);
//            userResponse.setMessage("user object is null.");
//            userResponse.setData(null);
//        }
//
//        return userResponse;
//    }
//
//    @ApiMethod(name="translate")
//    public TranslateResponse translate(@Named("word") String word, @Named("identifier") String androidId) {
//        TranslateResponse translateResponse = new TranslateResponse();
//        translateResponse.setCode(404);
//        translateResponse.setWord(null);
//
//        if(androidId != null && word != null) {
//            Entity wordEntity = new Entity("Word");
//            wordEntity.setProperty("word", word);
//            wordEntity.setProperty("android_id", androidId);
//
//            mDatastore.put(wordEntity);
//
//            translateResponse.setWord(word);
//            translateResponse.setCode(200);
//        }
//
//        return translateResponse;
//    }

    
    @ApiMethod(name = "register", httpMethod = ApiMethod.HttpMethod.POST)
    public DeviceObject register(DeviceObject data) throws ConflictException {
        if(data != null && data.getId() != null) {
            if(recordExistsOrNot(DeviceObject.class, data.getId())) {
                throw new ConflictException("already registered.");
            }
        }        
        
        data.setCreateDate(new Date());
        objectify().save().entity(data).now();
        return data;
    }
    
    @ApiMethod(name = "device", httpMethod = ApiMethod.HttpMethod.GET)
    public DeviceObject device(@Named("id") String id) throws NotFoundException {
        if(id == null || id == "") {
            throw new NotFoundException("you must specify id");
        }
        Query<DeviceObject> q = objectify().load().type(DeviceObject.class).filter("androidId", id);
        List<DeviceObject> collection = q.limit(1).list();
        if(collection == null && collection.size() <= 0) {
            throw new NotFoundException("no such device for this id");
        }
        return collection.get(0);
    }
    
    @ApiMethod(name = "sign", httpMethod = ApiMethod.HttpMethod.POST)
    public UserObject sign(UserObject data) throws ConflictException {
        if(data != null && data.getId() != null) {
            if(recordExistsOrNot(UserObject.class, data.getId())) {
                throw new ConflictException("User already registered.");
            }
        }
        
        data.setCreateDate(new Date());
        objectify().save().entity(data).now();
        return data;
    }
    
    @ApiMethod(name = "addTranslate",httpMethod = ApiMethod.HttpMethod.POST)
    public TranslateObject addTranslate(TranslateObject data) {
        data.setCreateDate(new Date());
        objectify().save().entity(data).now();
        return data;
    }
    
    private boolean recordExistsOrNot(Class clazz, Long id) {
        return objectify().load().type(clazz).id(id).now() != null;
    }
    
}
