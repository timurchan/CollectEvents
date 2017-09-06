package ru.timurchan.vkdata;

import org.json.JSONException;
import ru.timurchan.fedata.FeDataManager;
import ru.timurchan.fedata.ImportMeetingCreator;
import ru.timurchan.fedata.Meeting;
import ru.timurchan.model.Event;
import ru.timurchan.utils.MyHttpURLConnection;
import ru.timurchan.utils.MyUtils;

import java.util.*;

/**
 * Created by Timur on 30.01.2017.
 */
public class VkEventsManager implements MyHttpURLConnection.ConnectionListener {
    static public final boolean VK_VERSION_USING = false;

    private int MEETINGS_COUNT_TO_SEND = 5;
    final private int STEP_PERCENT = 5;

    private int mCounterAgain = 0;
    private int mEmptyGroupsAnswer = 0;
    private int mFriendsAnalysedCounter = 0;
    private ArrayList<Integer> mFriendsProcessed = new ArrayList<>();
    private int mQueueCounter = 1;

    private int mFriendsCount = 0;

    private Map<Integer, Event> mEvents = new TreeMap<>();
    private Set<Integer> mEventIds = new TreeSet<>();

    private ArrayList<Meeting> mMeetings = new ArrayList<>();

    private Thread mCollectEventsThread;

    private FeDataManager feDataManager = new FeDataManager();

    private EventsListener mListener;
    private VkDataCollector mDataCollector;
    private ImportMeetingCreator meetingCreator = new ImportMeetingCreator();

    private Set<Integer> mLoadedProcessedFriends = new TreeSet<>();
    private Set<Integer> mLoadedProcessedEventIds = new TreeSet<>();

    private VkEventsParser mEventsParser;

    public interface EventsListener {
        void OnUpdateEventsCount(int count);

        void OnUpdateFEProcessedFriendsPercent(int count);
        void OnUpdateFEProcessedFriendsCount(int count);

        void OnPreviousProcessedFriendsLoaded(int countFrinds, int countEvents);
    }

    public VkEventsManager(EventsListener listener,
                           VkDataCollector dataCollector) {
        mListener = listener;
        mDataCollector = dataCollector;
        mEventsParser = new VkEventsParser();
    }

    public void loadProcessedFriends() {
        mLoadedProcessedFriends = MyUtils.loadProcessedFriends();
        mLoadedProcessedEventIds = MyUtils.loadProcessedEventIds();
        if(mListener != null)
            mListener.OnPreviousProcessedFriendsLoaded(mLoadedProcessedFriends.size(), mLoadedProcessedEventIds.size());
    }

    public void setMeetingsPackCount(int count) {
        MEETINGS_COUNT_TO_SEND = count;
    }

    public void collectEvents(final Collection<String> friendsIds) {
        mCollectEventsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                collectEventsInternal(friendsIds);
            }
        });
        mCollectEventsThread.start();
    }

    public void collectEventsInternal(final Collection<String> friendsIds) {
        System.out.println("Total friends: " + friendsIds.size());
        mFriendsCount = friendsIds.size();
        mEventIds.clear();
        int maxFriends = 1;
        Integer percent = 0;
        if (mListener != null) {
            mListener.OnUpdateFEProcessedFriendsPercent(0);
            mListener.OnUpdateFEProcessedFriendsCount(0);
        }
        for (String id : friendsIds) {
            if(!mLoadedProcessedFriends.contains(Integer.valueOf(id))) {
                try {
                    MyUtils.sleepT();
                } catch (InterruptedException e) {
                    System.out.println("Stop collecting events. Total events: " + mEventIds.size());
                    return;
                }
                getEvent(id);
//                if (mFriendsAnalysedCounter % 500 == 0 ||
//                        mFriendsAnalysedCounter == friendsIds.size()) {
//                    saveProcessedEvents(false);
//                    saveProcessedFriends();
//                }
            }
//            if(mFriendsAnalysedCounter > maxFriends)
//                break;
            mFriendsAnalysedCounter++;

            Integer oldPercent = percent;
            percent = 100 * mFriendsAnalysedCounter / mFriendsCount;
            if (percent % STEP_PERCENT == 0 && !percent.equals(oldPercent)) {
                System.out.println(percent + "% processed. Time is " + MyUtils.getCurrentTimeStamp());
            }
            if (percent % 1 == 0 && !percent.equals(oldPercent)) {
                if (mListener != null)
                    mListener.OnUpdateFEProcessedFriendsPercent(percent);
            }
            if (mFriendsAnalysedCounter % 10 == 0 ||
                    mFriendsAnalysedCounter == friendsIds.size()) {
                if (mListener != null)
                    mListener.OnUpdateFEProcessedFriendsCount(mFriendsAnalysedCounter);
            }
        }
        sendMeetings();
        System.out.println("All friends processed");
    }

    private void getEvent(final String user_id) {
        //System.out.println("Start getEvent() for " + user_id);
        String targetURL = VkUrlProvider.requestGroups(user_id);
        MyHttpURLConnection.executeSendAsync(targetURL, this, MyHttpURLConnection.GROUPS_TYPE, user_id);
    }


    private void parseEventsResponse(final String answer, final String userId) {
        if (answer.equals("{\"response\":[0]}")) {
            mEmptyGroupsAnswer++;
            //System.out.println("parseEventsResponse() : mEmptyGroupsAnswer = " + mEmptyGroupsAnswer);
        } else {
            try {
                ArrayList<Event> futureEvents = mEventsParser.parseEvents(answer, userId);
                int futureEventsSize = futureEvents.size();
                for(Event event : futureEvents) {
                    processFutureEvent(event);
                }

                if (mListener != null)
                    mListener.OnUpdateEventsCount(mEventIds.size());

                if (futureEventsSize > 0) {
                    logInfo(userId, futureEventsSize);
                }
            } catch (JSONException e) {
                // for trying again if ERROR_TOO_MANY_REQUESTS occured
                String errUserId = mEventsParser.parseErrorResponse(answer);

                if (errUserId == null)
                    System.out.println(answer);

                if (errUserId != null && !errUserId.isEmpty()) {
                    MyUtils.sleep();
                    getEvent(errUserId);
                }

            }
        }
    }


    private void processFutureEvent(final Event event) {
        Integer eventId = event.id;
        boolean in_current = mEventIds.contains(eventId);
        boolean in_loaded = mLoadedProcessedEventIds.contains(eventId);
        if (!in_current && !in_loaded) {
            System.out.println(" - FE - " + event.name);
            mEvents.put(eventId, event);
            // eventId must be integer. If crash, than see API
            mEventIds.add(eventId);
            mMeetings.add(meetingCreator.createMeeting(event, mDataCollector.getMeetingTag()));
        } else {
            mCounterAgain++;
        }

        if (mMeetings.size() >= MEETINGS_COUNT_TO_SEND) {
            sendMeetings();
            mQueueCounter++;
        }
    }

    public void logInfo(final String userId, int counter) {
        System.out.println("parseEvents() for " + userId + " : collected " + counter + " future events. Total Events: " +
                mEventIds.size() + ". Duplicates: " + mCounterAgain + ". PermissionDenied: " + mEventsParser.getPermissionDeniedCounter() +
                ". Banned/Deleted: " + mEventsParser.getUserUnavailable() + ". Empty groupes: " + mEmptyGroupsAnswer +
                ". Friends processed: " + mFriendsProcessed.size() +
                ". Friends analysed: " + mFriendsAnalysedCounter + " / " + mFriendsCount +
                " (" + mFriendsAnalysedCounter / mFriendsCount + ")");
    }

    public void sendMeetings() {
        ArrayList<Meeting> meetings = new ArrayList<>();
        meetings.addAll(mMeetings);
        feDataManager.sendMeetings(meetings);
        mMeetings.clear();
    }


    @Override
    public void OnFriendsResponse(String response, final String arg1) {

    }

    @Override
    public void OnGroupsResponse(String response, final String arg1) {
        mFriendsProcessed.add(Integer.valueOf(arg1));
        parseEventsResponse(response, arg1);
    }





    public void stopGettingEvents() {
        if (mCollectEventsThread != null)
            mCollectEventsThread.interrupt();
        mFriendsAnalysedCounter = 0;


        saveProcessedFriends();
        saveProcessedEvents(true);
    }

    private void saveProcessedFriends() {
        // добавляем к ранее загруженным френдам оработанные за сеанс друзья
        String str = "mLoadedProcessedFriends.size() = { was " + mLoadedProcessedFriends.size();
        mLoadedProcessedFriends.addAll(mFriendsProcessed);
        str += " | become " + mLoadedProcessedFriends.size() + "}";
        System.out.println(str);
        Set<Integer> friendsIds = new TreeSet<>(mLoadedProcessedFriends);
        MyUtils.saveProcessedFriends(friendsIds);
    }

    private void saveProcessedEvents(boolean clearEvents) {
        // аналогично с уже отправленным событиями
        String str = "mLoadedProcessedEventIds.size() = { was " + mLoadedProcessedEventIds.size();
        mLoadedProcessedEventIds.addAll(mEventIds);
        str += " | become " + mLoadedProcessedEventIds.size() + "}";
        System.out.println(str);
        Set<Integer> eventsIds = new TreeSet<>(mLoadedProcessedEventIds);
        MyUtils.saveProcessedEventIds(eventsIds);
        if(clearEvents)
            mEventIds.clear();
    }
}
