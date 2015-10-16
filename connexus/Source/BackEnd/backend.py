import endpoints

import ImageAPIs
import StreamAPIs

APPLICATION = endpoints.api_server([StreamAPIs.StreamAPI, ImageAPIs.ImageAPI])