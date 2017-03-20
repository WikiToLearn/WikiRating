# WikiRating

## Synopsis:

The pages of content on wiki-style platforms can be more or less reliable and of good quality. But it all depends on parameters like author's credibility and interconnections between the pages. This project is aimed to develop a Rating Engine based on various parameters like User's Opinion, Author's Credibility,Page Interconnections and Revision History to evaluate the reliability and quality of articles and pages on such wiki-style learning platform.

## Algorithm:

To generate the ratings for a particular page the first step is to narrow down the parameters that will affect the quality of a page on the wiki platform.These parameters can affect the pages directly or indirectly. After deciding the parameters equations and methods have to be designed to use the parameters in a mathematical accurate way to cook up the ratings.

## Design:

The project is designed in a bipartite manner, these components are:

#### [WikiRating Engine](https://github.com/WikiToLearn/WikiRating)
The engine is the chief component of our project. All the data collection , processing and computation are done here. The Engine will be deployed in the form of a REST API. This engine will be requested for the necessary information like **Page Rating**,  **User Votes** , **Badges**  when the user visits a particular page.

Also this API can be further used to request the contributions of the users on a set of pages, this functionality is instrumental in designing a credit system for all the contributors.

#### [WikiRating Extension](https://github.com/WikiToLearn/WikiRatingExtension)
Most of the popular wiki - style platforms uses MediaWiki to host their services. WikiToLearn also uses MediaWiki. Now MediWiki uses some special kind of plugins to extend it's functionality and hence they are called **MediaWiki Extensions**.

Therefore we need to develop an extension to  not only show  visitors the ratings of the current page but also to process their votes for that page.This extension will serve as the linking mechanism between our engine and the wiki platform.

## GSOC 2016
The project was developed for the first time as a GSOC project in 2016. The relative resources can be found at these links. 
A great thank to Abhimanyu Singh Shekhawat for his contribution!

 - [Project page:KDE Community](https://community.kde.org/GSoC/2016/StatusReports/AbhimanyuSinghShekhawat)
 - [Project page:Google](https://summerofcode.withgoogle.com/projects/#6245052963618816)
 - [WikiRating Algorithm](https://drive.google.com/file/d/0B-aEMI94tcY8c1g3SmQzcGtVcXM/view)
 - [MediaWiki API Queries](https://drive.google.com/file/d/0B-aEMI94tcY8T3BGV0pyamhOUGc/view)
 - [WikiRating Extension](https://github.com/WikiToLearn/WikiRatingExtension)
