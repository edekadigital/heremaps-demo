# heremaps-demo

## Purpose

This demo project was created to face the following stackoverflow issue:

[Here SDK Resize](https://stackoverflow.com/questions/65610835/here-sdk-android-resize-on-layout-changes)

## Run

Add an here sdk for android aar file `into /app/libs`

Insert your here sdk id and key into apps manifest file

```
 <!-- credentials for the HERE SDK as it is used by multiple modules -->
        <meta-data
            android:name="com.here.sdk.access_key_id"
            android:value="YOUR ID" />
        <meta-data
            android:name="com.here.sdk.access_key_secret"
            android:value="YOUR KEY" />
```