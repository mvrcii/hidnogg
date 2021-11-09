# Definitely-not-HidNogg.github.io

**Run Configuration:**
Add the following line of code to the VM-Options:

`--module-path C:\Users\Marce\IdeaProjects\hidnogg\libraries\JavaFx\javafx-sdk-11.0.2\lib --add-modules javafx.controls,javafx.fxml,javafx.media`

---

**Libraries:** Add the following two libraries:

`File >> Project Structure >> Libraries >> + >> Java >> Select the lib folder: path\to\hidnogg\libraries\JavaFx\javafx-sdk-11.0.2\lib`
 
It should then look like this: 

![grafik](https://user-images.githubusercontent.com/56437044/140950956-6b87447a-9586-4d4a-9be1-4003a919ae59.png)


`File >> Project Structure >> Libraries >> + >> Java >> Select all files inside the lib folder: path\to\hidnogg\libraries\tinysound-lib\lib` 

It should then look like this:

![grafik](https://user-images.githubusercontent.com/56437044/140950882-96eb6e7b-1803-4e64-b875-86838692383d.png)

---

**Modules:** First of all, add both libraries `JavaFx` and `tinysound` to the modules:

`File >> Project Structure >> Modules >> Dependencies (Tab on the right side) >> + >> Libraries >>`

It then should look like this (**make sure that the Export check marks are checked**):
![grafik](https://user-images.githubusercontent.com/56437044/140953627-248bccdd-2fda-4f2b-b59c-07a0ec82af37.png)

Also make sure that the Module SDK you have selected is the same version as the Project SDK, in this case **15**.

---

**Project SDK:** The project version has to be 15. First add the JDK and then select it:

`File >> Project Structure >> Platform Settings >> SDKs >> + >> Add JDK >> Select the Path (for example: D:\Java\jdk-15.0.2)`

`File >> Project Structure >> Project Settings >> Project >> Project SDK >> Select Project SDK 15`
