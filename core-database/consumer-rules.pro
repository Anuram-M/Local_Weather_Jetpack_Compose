# Keep Gson's internal generic type parsing mechanics intact
-keepattributes Signature, *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }

# Prevent R8 from renaming or stripping your Room data models
-keep class com.ram.core_database.dto.MappedForecast { *; }
-keep class * extends com.google.gson.reflect.TypeToken