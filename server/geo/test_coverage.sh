
<!-- saved from url=(0092)https://bitbucket.org/cuongthai/knoowme/raw/ba0095fb7e3e/knoowme/server/geo/test_coverage.sh -->
<html><head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></head><body><pre style="word-wrap: break-word; white-space: pre-wrap;">#!/bin/sh

# NOTE: requires coverage.py module
coverage -e
coverage -x geomath_test.py
coverage -x geotypes_test.py
coverage -x util_test.py
coverage -x geocell_test.py

coverage -r -m geomath.py geotypes.py util.py geocell.py
</pre></body></html>