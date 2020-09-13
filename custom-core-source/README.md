The F.D.A.F Framework Custom Core Source
==============================================

If you want to add additional custom core sources, you should work within
this directory and keep the main `../core-source` untouched. You may add
i.e your own `base/src/main/java/your_custom_package` directory and all
sources within, and so on. The F.D.A.F framework `initializer` will
combine your customized sources with any other generated compilable
sources.
