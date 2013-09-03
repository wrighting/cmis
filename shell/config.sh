if [ "$(uname)" = 'CYGWIN_NT-6.1-WOW64' ]
then
 SEPARATOR=';'
else
 SEPARATOR=':'
fi

CLASSPATH=.
for i in `ls jars/*`
do
        CLASSPATH="$CLASSPATH$SEPARATOR./$i"
done
echo classpath ${CLASSPATH}
