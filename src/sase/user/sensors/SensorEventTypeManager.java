package sase.user.sensors;

import sase.base.Attribute;
import sase.base.Datatype;
import sase.base.Event;
import sase.base.EventType;
import sase.pattern.EventTypesManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorEventTypeManager extends EventTypesManager {

    public static final int labelAttributeIndex = 34;
    public static final int timestampAttributeIndex = 0;
    public static final int firstSensorIDdataIndex = 1;
    public static final int complexityDataIndex = 8;
    public static final int activityChangeIndex = 9;
    public static final int areaTransitionsIndex = 10;
    public static final int numSensorsIndex = 11;
    public static final int weightedSensorCountFirstIndex = 12;


    public static final String sleepEventTypeName = "Sleep";
    public static final String otherActivityEventTypeName = "Other_Activity";
    public static final String toiletEventTypeName = "Toilet";
    public static final String bedToiletTransitionEventTypeName = "Bed_Toilet_Transition";
    public static final String personalHygieneEventTypeName = "Personal_Hygiene";
    public static final String entertainGuestsEventTypeName = "Entertain_Guests";
    public static final String leaveHomeEventTypeName = "Leave_Home";
    public static final String enterHomeEventTypeName = "Enter_Home";
    public static final String stepOutEventTypeName = "Step_Out";
    public static final String phoneEventTypeName = "Phone";
    public static final String sleepOutOfBedEventTypeName = "Sleep_Out_Of_Bed";
    public static final String workEventTypeName = "Work";
    public static final String workAtTableEventTypeName = "Work_At_Table";
    public static final String cookDinnerEventTypeName = "Cook_Dinner";
    public static final String eatDinnerEventTypeName = "Eat_Dinner";
    public static final String watchTVEventTypeName = "Watch_TV";
    public static final String groomEventTypeName = "Groom";
    public static final String dressEventTypeName = "Dress";
    public static final String readEventTypeName = "Read";
    public static final String workOnComputerEventTypeName = "Work_On_Computer";
    public static final String washDinnerDishesEventTypeName = "Wash_Dinner_Dishes";
    public static final String washDishesEventTypeName = "Wash_Dishes";
    public static final String cookBreakfastEventTypeName = "Cook_Breakfast";
    public static final String drinkEventTypeName = "Drink";
    public static final String eatBreakfastEventTypeName = "Eat_Breakfast";
    public static final String cookLunchEventTypeName = "Cook_Lunch";
    public static final String batheEventTypeName = "Bathe";
    public static final String washBreakfastDishedEventTypeName = "Wash_Breakfast_Dishes";
    public static final String RelaxEventTypeName = "Relax";
    public static final String eveningMedsEventTypeName = "Evening_Meds";
    public static final String morningMedsEventTypeName = "Morning_Meds";
    public static final String washLunchDishesEventTypeName = "Wash_Lunch_Dishes";
    public static final String cookEventTypeName = "Cook";
    public static final String eatLunchEventTypeName = "Eat_Lunch";
    public static final String eatEventTypeName = "Eat";

    public static EventType sleepEventType;
    public static EventType otherActivityEventType;
    public static EventType toiletEventType;
    public static EventType bedToiletTransitionEventType;
    public static EventType personalHygieneEventType;
    public static EventType entertainGuestsEventType;
    public static EventType leaveHomeEventType;
    public static EventType enterHomeEventType;
    public static EventType stepOutEventType;
    public static EventType phoneEventType;
    public static EventType sleepOutOfBedEventType;
    public static EventType workEventType;
    public static EventType workAtTableEventType;
    public static EventType cookDinnerEventType;
    public static EventType eatDinnerEventType;
    public static EventType watchTVEventType;
    public static EventType groomEventType;
    public static EventType dressEventType;
    public static EventType readEventType;
    public static EventType workOnComputerEventType;
    public static EventType washDinnerDishesEventType;
    public static EventType washDishesEventType;
    public static EventType cookBreakfastEventType;
    public static EventType drinkEventType;
    public static EventType eatBreakfastEventType;
    public static EventType cookLunchEventType;
    public static EventType batheEventType;
    public static EventType washBreakfastDishedEventType;
    public static EventType RelaxEventType;
    public static EventType eveningMedsEventType;
    public static EventType morningMedsEventType;
    public static EventType washLunchDishesEventType;
    public static EventType cookEventType;
    public static EventType eatLunchEventType;
    public static EventType eatEventType;


    public List<String> getAttributeNames() {
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add("timestamp");
        attributeNames.add("windowDuration");
        attributeNames.add("timeSinceLastSensorEvent");
        attributeNames.add("prevDominantSensor1");
        attributeNames.add("prevDominantSensor2");
        attributeNames.add("lastSensorID");
        attributeNames.add("lastSensorLocation");
        attributeNames.add("lastMotionLocation");
        attributeNames.add("complexity");
        attributeNames.add("activityChange");
        attributeNames.add("areaTransitions");
        attributeNames.add("numDistinctSensors");
        attributeNames.add("sensorCountBathroom");
        attributeNames.add("sensorCountBedroom");
        attributeNames.add("sensorCountChair");
        attributeNames.add("sensorCountDiningRoom");
        attributeNames.add("sensorCountHall");
        attributeNames.add("sensorCountIgnore");
        attributeNames.add("sensorCountKitchen");
        attributeNames.add("sensorCountLivingRoom");
        attributeNames.add("sensorCountOffice");
        attributeNames.add("sensorCountOutsideDoor");
        attributeNames.add("sensorCountWorkArea");
        attributeNames.add("sensorElTimeBathroom");
        attributeNames.add("sensorElTimeBedroom");
        attributeNames.add("sensorElTimeChair");
        attributeNames.add("sensorElTimeDiningRoom");
        attributeNames.add("sensorElTimeHall");
        attributeNames.add("sensorElTimeIgnore");
        attributeNames.add("sensorElTimeKitchen");
        attributeNames.add("sensorElTimeLivingRoom");
        attributeNames.add("sensorElTimeOffice");
        attributeNames.add("sensorElTimeOutsideDoor");
        attributeNames.add("sensorElTimeWorkArea");
        attributeNames.add("activity");
        return attributeNames;

    }

    @Override
    public String getEventLabel(Event event) {
        return (String)event.getAttributeValue(labelAttributeIndex);
    }

    @Override
    public Long getEventTimestamp(Event event) {
        LocalDateTime ldt = LocalDateTime.parse((String)event.getAttributeValue(timestampAttributeIndex));
        return Long.parseLong(ldt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    @Override
    public Object[] convertStringPayloadToObjectPayload(Object[] payload) {
        Object[] newPayload = new Object[payload.length];
        Map<Integer, Datatype> indexToDataType = getMappingBetweenIndexToDataType();
        for (int i : indexToDataType.keySet()) {
            switch(indexToDataType.get(i)) {
                case LONG: // Actually double as the file stores as 5.0, but we convert to integer
                    Double textDouble = Double.valueOf((String) payload[i]);
                    if (Math.rint(textDouble) != textDouble) {
                        throw new RuntimeException("Converting double value to int");
                    }
                    newPayload[i] = textDouble.longValue();
                    break;
                case TEXT:
                    newPayload[i] = payload[i];
                    break;
                case DOUBLE:
                    newPayload[i] = Double.valueOf((String)payload[i]);
                    break;
                case LOCALDATETIME:
                    newPayload[i] = payload[i];
                    break;
                default:
                    throw new RuntimeException("There is a datatype that doesn't exists" + indexToDataType.get(i).toString());
            }
        }

//        newPayload[timestampAttributeIndex] = Long.valueOf((String)payload[timestampAttributeIndex]);
//        for (int i = firstSensorIDdataIndex; i < complexityDataIndex; ++i) { //Integers
//                newPayload[i] = Integer.valueOf((String)payload[i]);
//        }
//        newPayload[complexityDataIndex] = Double.valueOf((String)payload[complexityDataIndex]);
//        newPayload[activityChangeIndex] = Double.valueOf((String)payload[activityChangeIndex]);
//        newPayload[areaTransitionsIndex] = Integer.valueOf((String)payload[areaTransitionsIndex]);
//        newPayload[numSensorsIndex] = Integer.valueOf((String)payload[numSensorsIndex]);
//        for (int i = weightedSensorCountFirstIndex; i < labelAttributeIndex; i++) {
//            newPayload[i] = Double.valueOf((String) payload[i]);
//        }
//        newPayload[labelAttributeIndex] = payload[labelAttributeIndex];
        return newPayload;
    }


    @Override
    public long getAverageEventSize() {
        // 8 integers (4 bytes), 28 doubles (8 bytes) and the activity label which is 10 chars average (2 bytes each),
        // and timestamp is a long (8 bytes)
        return 8 * 28 + 4 * 8 +  10 * 2 + 8;
    }

    @Override
    public List<EventType> getKnownEventTypes() {
        List<EventType> knownEventTypes = new ArrayList<>();
        knownEventTypes.add(sleepEventType);
        knownEventTypes.add(otherActivityEventType);
        knownEventTypes.add(toiletEventType);
        knownEventTypes.add(bedToiletTransitionEventType);
        knownEventTypes.add(personalHygieneEventType);
        knownEventTypes.add(entertainGuestsEventType);
        knownEventTypes.add(leaveHomeEventType);
        knownEventTypes.add(enterHomeEventType);
        knownEventTypes.add(stepOutEventType);
        knownEventTypes.add(phoneEventType);
        knownEventTypes.add(sleepOutOfBedEventType);
        knownEventTypes.add(workEventType);
        knownEventTypes.add(workAtTableEventType);
        knownEventTypes.add(cookDinnerEventType);
        knownEventTypes.add(eatDinnerEventType);
        knownEventTypes.add(watchTVEventType);
        knownEventTypes.add(groomEventType);
        knownEventTypes.add(dressEventType);
        knownEventTypes.add(readEventType);
        knownEventTypes.add(workOnComputerEventType);
        knownEventTypes.add(washDinnerDishesEventType);
        knownEventTypes.add(washDishesEventType);
        knownEventTypes.add(cookBreakfastEventType);
        knownEventTypes.add(drinkEventType);
        knownEventTypes.add(eatBreakfastEventType);
        knownEventTypes.add(cookLunchEventType);
        knownEventTypes.add(batheEventType);
        knownEventTypes.add(washBreakfastDishedEventType);
        knownEventTypes.add(RelaxEventType);
        knownEventTypes.add(eveningMedsEventType);
        knownEventTypes.add(morningMedsEventType);
        knownEventTypes.add(washLunchDishesEventType);
        knownEventTypes.add(cookEventType);
        knownEventTypes.add(eatLunchEventType);
        knownEventTypes.add(eatEventType);
        return knownEventTypes;
    }

    @Override
    public List<String> getKnownEventTypeNames() {
        ArrayList<String> eventTypeNames = new ArrayList<>();
        for (EventType eventTypeName : getKnownEventTypes()) {
            eventTypeNames.add(eventTypeName.getName());
        }
        return eventTypeNames;
    }


    public Map<Integer, Datatype> getMappingBetweenIndexToDataType() {
        Map<Integer, Datatype> indexToDataType = new HashMap<>();
        indexToDataType.put(timestampAttributeIndex, Datatype.LOCALDATETIME);
        for (int i = firstSensorIDdataIndex; i < complexityDataIndex; ++i) { //Integers
            indexToDataType.put(i, Datatype.LONG);
        }
        indexToDataType.put(complexityDataIndex, Datatype.DOUBLE);
        indexToDataType.put(activityChangeIndex, Datatype.DOUBLE);
        indexToDataType.put(areaTransitionsIndex, Datatype.LONG);
        indexToDataType.put(numSensorsIndex, Datatype.LONG);
        for (int i = weightedSensorCountFirstIndex; i < labelAttributeIndex; i++) {
            indexToDataType.put(i, Datatype.DOUBLE);
        }
        indexToDataType.put(labelAttributeIndex, Datatype.TEXT);
        return indexToDataType;
    }
    @Override
    protected void actuallyInitializeTypes() {
        Attribute[] attributes = new Attribute[labelAttributeIndex + 1];
        Map<Integer, Datatype> indexToDataType = getMappingBetweenIndexToDataType();
        List<String> attributeNames = getAttributeNames();
        for (int i : indexToDataType.keySet()) {
            attributes[i] = new Attribute(indexToDataType.get(i), attributeNames.get(i));
        }

        sleepEventType = new EventType(sleepEventTypeName,attributes);
        otherActivityEventType = new EventType(otherActivityEventTypeName, attributes);
        toiletEventType = new EventType(toiletEventTypeName, attributes);
        bedToiletTransitionEventType = new EventType(bedToiletTransitionEventTypeName, attributes);
        personalHygieneEventType = new EventType(personalHygieneEventTypeName, attributes);
        entertainGuestsEventType = new EventType(entertainGuestsEventTypeName, attributes);
        leaveHomeEventType = new EventType(leaveHomeEventTypeName, attributes);
        enterHomeEventType = new EventType(enterHomeEventTypeName, attributes);
        stepOutEventType = new EventType(stepOutEventTypeName, attributes);
        phoneEventType = new EventType(phoneEventTypeName, attributes);
        sleepOutOfBedEventType = new EventType(sleepOutOfBedEventTypeName, attributes);
        workEventType = new EventType(workEventTypeName, attributes);
        workAtTableEventType = new EventType(workAtTableEventTypeName, attributes);
        cookDinnerEventType = new EventType(cookDinnerEventTypeName, attributes);
        eatDinnerEventType = new EventType(eatDinnerEventTypeName, attributes);
        watchTVEventType = new EventType(watchTVEventTypeName, attributes);
        groomEventType = new EventType(groomEventTypeName, attributes);
        dressEventType = new EventType(dressEventTypeName, attributes);
        readEventType = new EventType(readEventTypeName, attributes);
        workOnComputerEventType = new EventType(workOnComputerEventTypeName, attributes);
        washDinnerDishesEventType = new EventType(washDinnerDishesEventTypeName, attributes);
        washDishesEventType = new EventType(washDishesEventTypeName, attributes);
        cookBreakfastEventType = new EventType(cookBreakfastEventTypeName, attributes);
        drinkEventType = new EventType(drinkEventTypeName, attributes);
        eatBreakfastEventType = new EventType(eatBreakfastEventTypeName, attributes);
        cookLunchEventType = new EventType(cookLunchEventTypeName, attributes);
        batheEventType = new EventType(batheEventTypeName, attributes);
        washBreakfastDishedEventType = new EventType(washBreakfastDishedEventTypeName, attributes);
        RelaxEventType = new EventType(RelaxEventTypeName, attributes);
        eveningMedsEventType = new EventType(eveningMedsEventTypeName, attributes);
        morningMedsEventType = new EventType(morningMedsEventTypeName, attributes);
        washLunchDishesEventType = new EventType(washLunchDishesEventTypeName, attributes);
        cookEventType = new EventType(cookEventTypeName, attributes);
        eatLunchEventType = new EventType(eatLunchEventTypeName, attributes);
        stepOutEventType = new EventType(stepOutEventTypeName, attributes);
        eatEventType = new EventType(eatEventTypeName, attributes);
    }

    @Override
    protected HashMap<String, EventType> createNameToTypeHash() {
        HashMap<String, EventType> nameToTypeHash = new HashMap<String, EventType>();
        for (EventType eventType : getKnownEventTypes()) {
            nameToTypeHash.put(eventType.getName(), eventType);
        }
        return nameToTypeHash;
    }

    @Override
    protected HashMap<String, String> createLongNameToShortNameHash() {
        HashMap<String, String> sameNameMap = new HashMap<String, String>();
        for (String eventTypeName : getKnownEventTypeNames()) {
            sameNameMap.put(eventTypeName, eventTypeName);
        }
        return sameNameMap;
    }

    @Override
    public List<String> getAllFusedTypeNames() {
        System.out.println("Not handled fusion yet");
        return new ArrayList<>();
    }
}
