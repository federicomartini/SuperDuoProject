# SuperDuoProject
Bugfix and Productionize Project

##Alexandria App

### Version
The minimum SDK version is set to 14 and the maximum is 21. Be sure to have 'com.android.support:appcompat-v7:21.0.3' and 'me.dm7.barcodescanner:zbar:1.8.2' support.

### Log
- No connectivity crash when looking for new book fixed.
- Added ZBar Library for Scanner functionality
- Fixed ISBN-13 build starting from ISBN-10. The control number has to be recalculated with proper algorithm.
- Fixed possible crash triggered by null authors after 'onLoadFinished' within AddBook.java and BookDetail.java
- Fixed crashe caused by shareActionProvider when rotating the App in Tablet version
- Fixed wrong behaviour in Tablet configuration that put the List into Left Container when passing from Portrait to Landscape orientation
- Fixed an issue that prevent keeping current Book Detail to be moved from Container to right_container when passing from Portrait to Landscape
- Fixed an issue that caused a warning when passing an invalid URL to the DownloadImage in the BookListAdapter.
- Fixed an issue that caused app crash when deleting a Book and the ean was Empty but not Null.
- Fixed an issue that prevents to remove Book title from the preview when adding the book.
- Fixed an issue that kept on the screen the previous image preview when the next result didn't have an Image available.

##Football Scores App

### Version
The minimum SDK version is set to 14 and the maximum is 21. Be sure to have 'com.android.support:appcompat-v7:21.0.3' support.

### Widget
- This application is provided to you with a widget available to show you the 'Today Scores'.

- Collection Widget Added (the layout is pretty basic and is something like a mockup to debug the functionality)
- Clicking on an item on the Collection Widget will open the relative Match Detail within the Activity.
- No Api-Key added in order to keep the source code safe and to avoid beaking Terms of Service
- Fixed behavior of the resuming status of the app
- Added support for RTL
- Added support for Accessibility through TalkBack
- Fix wrong behavior when having a detail view expanded that caused to possibly have two detail view opened at the same time.
- Added management of PagerFragment.java when RTL is enabled
- Put strings in strings.xml file
- Handled Refresh Button on the Collection widget.
- Added ViewHolder class to ScoresAdapter

