Installation for React-Native

1. Installing the module

        npm install --save git+ssh://git@github.com:ShabanovPavel/GPSLocator-Android.git

2. Pulling dependencies 

        yarn

3. In AndroidManifest.xml add

    Permissions 

        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    
    
    And register a service
    
        <application
            ...
         <service
            android:name="com.ads_design.gps_locator.NetworkService"
            android:enabled="true"
            android:exported="true" />
         <meta-data android:name="AA_DB_NAME" android:value="GPSStorage" />
         <meta-data android:name="AA_DB_VERSION" android:value="1" />
            ...
        </application>
         


4. In Build.gradle ( ../android/app/) add

        android {
            ...
            repositories {
                mavenCentral()
                maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
            }
            ...
        }

        dependencies {
            ...
            compile project(':shp-gps-locator')
            ...
        }


5. In Settings.gradle add

        include ':shp-gps-locator'
        project(':shp-gps-locator').projectDir = new File(rootProject.projectDir, '../node_modules/shp-gps-locator/android')

6. In MainApplication.java  add
    
        import com.shp.gps_locator.PackageGPSLocator;

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                ...
                new PackageGPSLocator()
                ...
            );
        }


7. Using

```javascript

        import {GPSLocator} from 'shp-gps-locator';

        //Settings
        //Setting the interval in which you want to receive data (ms) by default 10 * 1000
        let rez=await GPSLocator.setInterval(10*1000);
        //Setting an interval in which you can get data if another application received them earlier (ms) by default 5 * 1000
        let rez=await GPSLocator.setFastestInterval(5*1000);
        //Setting the minimum distance to update the gps (meters) data by default is 1 meter
        let rez=await GPSLocator.setSmallestDisplacement(1);

        //Use
        //Start service - The first parameter is the user's ID, the second server address for the transfer of gps data
        let rez=await GPSLocator.runGPS('id','http://your.server/');
        //Stop service
        let rez=await GPSLocator.stopGPS();

```

