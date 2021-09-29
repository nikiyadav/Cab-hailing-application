#! /bin/sh
cd TestCases
for f in *.sh; do
  echo "---$f-------"

  sh "$f" || break
  echo "------------------------------"
done
