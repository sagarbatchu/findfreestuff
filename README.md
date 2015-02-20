# Find Free Stuff

Find Free Stuff is an Android application in development by Team 1 for CS121: Software Development being taught in the Spring of 2015 at Harvey Mudd College by Jessica Wu. The goal of this application is to provide a platform for to connect people who are looking to get rid of things they own but do not wish to without incurring waste or expense with people who are looking to acquire new things for free.

## Requirements
This project relies on the [Google Play Services SDK](https://developer.android.com/google/play-services/setup.html), and the [Parse SDK](https://parse.com/apps/quickstart#parse_data/mobile/android/native/existing) (create the project/app/libs folder if it does not yet exist). This also relies on the Google Maps API to be properly set up for this app with your computer. First, navigate to [this](https://developers.google.com/maps/documentation/android/start#display_your_apps_certificate_information) page and go through the section "Displaying the debug certificate fingerprint". From this, copy down the "SHA1" portion of the output. Go to the [Google Developers Console](https://console.developers.google.com/project) and sign in (with the project email, the project Find Free Stuff should already exist). Select the Find Free Stuff project, and under the left menu bar select "APIs & auth/Credentials". From this page, you will see a button to "Create new Key". Press this button and when prompted, select the "Android Key" button. Then, in the space provided enter the "SHA1" output you copied earlier followed by ";edu.hmc.sp15.cs121.findfreestuff" and press the "Create" button. This should return you to the "Credentials" page, and the new "API KEY" should be displayed along with some additional information. Place the "API KEY" in you Android application as described [here](https://developers.google.com/maps/documentation/android/start#add_the_api_key_to_your_application).

## Development Notes
### Emulator GPS
To develop on this app, it is necessary to be able to move your current location on the Emulator. To do this, we use `telnet` to connect to our Emulator and then feed it commands to simulate different GPS locations. If we have one instance of the Emulator running it will be on port 5554, so the command we run to connect is `telnet localhost 5554`. Once connected to the emulator, we set the GPS location with the command `geo fix <longitude value> <latitude value>`.