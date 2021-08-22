# PGR208-Android-Eksamenbesvarelse
Eksamensbesvarelse i PGR208 Androidprogrammering ved Høyskolen Kristiania våren 2021

Dette er en eksamensbesvarelse i faget Android Programmering ved Høyskolen Kristiania våren 2021. 
Oppgave vår var å lage en app for kjøp og salg cryptovaluta. Appen skulle listet alle cryptoene fra coinbase apiet samt informasjonen om den nåværende kurs og prosentvis økning eller nedgang.
Oppgave ble sensusert til en B.


#Report PGR208 Exam

We think we covered all the screens included in the exam. There are pictures of the screens we have implemented further down on the next page. 

When you open the app you are automatically given the 10000 coins of Tether (USDT). We could have given dollars just as easily since it’s hardcoded directly into the database on the splashscreen. The reason we choose tether instead is because it’s value is supposedly directly tied to the United States dollar. We felt it was more in theme with the app give this value. 

This app consists of five activities. The first activity is a SplashScreen, it has a delay for 3 sec before it transfers you to the CoinStatsActivity, which is called Live stats on the apps navbar. Here you can find the real-time cryptocurrency rates. We have used Retrofit2 for fetching data from the API.
When clicking on a crypto you will get CoinStatActivity that will show a graph of the fluctuations in the value of currency over a given interval. We have handled this by using a QueueingService that posts messages to the main looper, combined by the handlers post delayed function, which gives us the opportunity to repeat a function with a given interval. Overrides in each activity, that utilizes this queueing service, clears the queue when we change activity/pause/restart. We have used EazeGraph as our graph library. In the Live chart we have added a value point for each response from the API. We wanted to use LocalDate instead of String in the data class, but since we ran out of time to do that we went for a substring.

CoinStatActivity consist of two fragments where you can buy or sell crypto. You need to edit the amount you want to buy or sell for. To see the transaction that has been done you need to navigate yourself to transaction. The last activity is the WalletContent that shows all the cryptos that you have bought. For showing the image we have used Glide. 

For the UI-components we have used the standard components as ConstraintLayout, LinearLayout, Recycler views,  ImageView, TextView, Button and toolbar, but we have also used material design for the bottom navigation. 

The database works like this: There are 2 Entities that are backed up by 2 dao’s. The dao’s are again backed up by a repository, to make it more abstract and not to expose the finer part of the database out of the database. Its then backed up with each its own viewmodel and each are sharing the same room database. Then the coroutines are added to the application (see the file cryptoapplication)

