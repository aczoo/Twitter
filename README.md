# Project 3 - Twitter

Twitter is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: 20? hours spent in total

## User Stories

The following **required** functionality is completed:

* [x]	User can **sign in to Twitter** using OAuth login
* [x]	User can **view tweets from their home timeline**
  * [x] User is displayed the username, name, and body for each tweet
  * [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
* [x] User can **compose and post a new tweet**
  * [x] User can click a “Compose” icon in the Action Bar on the top right
  * [x] User can then enter a new tweet and post this to Twitter
  * [x] User is taken back to home timeline with **new tweet visible** in timeline
  * [x] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
* [x] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [x] User can **pull down to refresh tweets timeline**
* [x] User can **see embedded image media within a tweet** on list or detail view.

The following **stretch** features are implemented:

* [x] User is using **"Twitter branded" colors and styles**
* [ ] User sees an **indeterminate progress indicator** when any background or network task is happening
  * [x] User sees an **result based progress indicator** when any background or network task is happening
* [x] User can **select "reply" from home timeline to respond to a tweet**
  * [x] User that wrote the original tweet is **automatically "@" replied in compose**
* [x] User can tap a tweet to **open a detailed tweet view**
  * [x] User can **take favorite (and unfavorite) or retweet** actions on a tweet
* [x] User can view more tweets as they scroll with infinite pagination
* [x] Compose tweet functionality is built using modal overlay
  * [x] Clicking outside of the fragment closes it
* [x] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [x] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.org/android/Drawables#vector-drawables) where appropriate.
* [ ] User can view following / followers list through any profile they view.
* [ ] Use the View Binding library to reduce view boilerplate.
* [ ] On the Twitter timeline, apply scrolling effects such as [hiding/showing the toolbar](http://guides.codepath.org/android/Using-the-App-ToolBar#reacting-to-scroll) by implementing [CoordinatorLayout](http://guides.codepath.org/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events).
* [ ] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.

The following **additional** features are implemented:

* [x] User can delete their tweet on the home page
  * [x] Holding down on a tweet will prompt a dialogfragment
* [x] User can also like and retweet another individual's tweet on their home page
* [x] Requests for extended tweet and the retweeted object
* [x] Details page displays expanded time and both the like/retweet count.

Future improvements:
- In the current Twitter dev API, there is no quick way to access a list of all replies to a tweet (Other than 3rd party sources and timely recursive functions). 
  - But one solution would be to use status/show to get the user's id. Then, use statuses/mentions_timeline to find any in_reply_to_status_id matching the original tweet's id
- Slight bug with the Compose Tweet API
  - Accessing the compose fragment through the action bar option and reply option worked fine: user would be prompted to tweet and upon posting it, the recycler view would update with the new post. However, when composing a reply in my details activity, the new tweet would replace the first tweet rather than bump it down.
  - Possible oddity from having duplicates of the same call in different classes ( Local Broadcast Manager and an onActivityResult)


## Video Walkthrough

Here's a walkthrough of implemented user stories:

![Demo](https://github.com/aczoo/Twitter/blob/master/demo.gif) ![Demo2](https://github.com/aczoo/Twitter/blob/master/demo%2012.23.22%20AM.gif)


## Notes
- reading the JSON files and determining how to extract certain values was somewhat tedious without the JSON formatting from Flixter
- had trouble with selectors and the different status options, ended up using an imageview and changing the source when clicked

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright [2020] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
