import endpoints

WEB_CLIENT_ID = '1050209727214-68oqhbc3l2fsfrbdup4dg08d4cmvusi8.apps.googleusercontent.com'
ANDROID_CLIENT_ID = '1050209727214-8tqfi4s5ck03nm3c3acabuodkhbriq94.apps.googleusercontent.com'
ANDROID_AUDIENCE = WEB_CLIENT_ID

ConnexusAPI = endpoints.api(name='connexusAPI', version='v1.0',
                            allowed_client_ids=[WEB_CLIENT_ID, ANDROID_CLIENT_ID,
                            endpoints.API_EXPLORER_CLIENT_ID],
                            audiences=[ANDROID_AUDIENCE],
                            scopes=[endpoints.EMAIL_SCOPE])
