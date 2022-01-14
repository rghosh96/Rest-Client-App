# Blog (Rest Client) App (Android)
This Android application is a blog post app called the Ipsum Blog that allows users to view posts, profiles, comments, and add comments. The purpose of the assignment was to gain familiarity with web service clients, such as sending and retrieving data with HTTP GET and POST requests. The final outcome is a REST Client interface that fetches data from a remote server, displays that information, and allows user to post data (in the form of comments) to the server as well.

## Description
In this project, Android Studio and the Java programming language was used to develop a mock blog app that retrieves, displays, and sends data to a remote server. The Retrofit Android HTTP client was utilized in order to make these requests. Ultimately, the Ipsum Blog application successfully performs the following actions:
1) Load a list view of all blog posts, displaying post ID,
title of blog post, and username
2) Allow user to tap username to view their profile, which
includes their name, username, email, phone, website,
location map, and a list of all the user’s posts
3) Allow user to tap title to see entire blog post (with name
of user and post body), and a list of comments associated
with that post
4) For each comment, display the email of the user who
submitted it, the comment title, and the comment body
5) Allow user to add a comment to a blog post (this in-
formation does not persist, but does display temporarily
while the user is on the post’s page)
