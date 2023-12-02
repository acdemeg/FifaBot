set JDEPS_COMMAND=jdeps --print-module-deps --ignore-missing-deps .\build\libs\Fifa19Bot-1.0.jar
@rem set list needs modules
for /f "delims=" %%i in ('%JDEPS_COMMAND%') do set MODULES=%%i
@rem create minimal runtime
jlink --add-modules %MODULES% --output .\build\AIbotRuntime
@rem move .jar in AIbotRuntime
cp .\build\libs\Fifa19Bot-1.0.jar .\build\AIbotRuntime
@rem create executable file
cd build
npx caxa --input .\AIbotRuntime --output FifaBot.exe --no-include-node -- "{{caxa}}/bin/java" "-jar" "{{caxa}}/FifaBot-1.0.jar"
