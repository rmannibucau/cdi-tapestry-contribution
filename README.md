CDI Tapestry Contribution
=========================

Goal
----

Be able to inject CDI bean in Tapestry pages.

Build
-----

    mvn clean install

Usage
-----

Simply inject your beans as if tapestry pages were managed by CDI.
Currently only fields are supported.

    @Inject
    private MyCdiBean bean;


