# Running the different projects

Note that the code is split in multiple different projects: `turnstile`, `broadcaster`, `boundedbuffer`, `primer` and `printersystem`. Each of these projects is located in a different package. Also, each project has a `Main.java` class which starts the execution. You can run a project with `$ gradle run -PmainClass=<project_package>.Main`, e.g., `$ gradle run -PmainClass=turnstile.Main`.
