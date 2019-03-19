# Perms

Perms is a small, fast and concise library to simplify requesting permissions.

## Instalation

Perms is still in developer version, so instalation isn't available yet.

## Usage

- `AcceptedResponse` will be called automatically on devices with SDK before 23.
- Empty request returns void.


### Java

```java
        new Perms(activity)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult(
                        new AcceptedResponse() {
                            @Override
                            public void onAllAccepted(List<String> acceptedPermissions) {
                                //Called on all accepted.
                            }
                        }, new DeniedResponse() {
                            @Override
                            public void onAtLeastOneDenied(List<String> deniedPermissions) {
                                //Called on at least one denied.
                            }
                        }, new ForeverDeniedResponse() {
                            @Override
                            public void onAtLeastOneForeverDenied(List<String> foreverDeniedPermissions) {
                                //Called on at least one forever denied.
                            }
                        }
                );
```

It is possible to call single `AcceptedResponse` or both `AcceptedResponse` and `DeniedResponse` also.

### Kotlin

#### Without extension

```kotlin
            Perms(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult(
                    { /* Called on all granted */ },
                    { /* Called on at least one denied */ },
                    { /* Called on at least one forever denied */ }
                )
```
#### With extension

I made an extension for more readable usage.

```kotlin
fun Perms.onResult(
    onAllAccepted: (List<String>) -> Unit = {},
    onAtLeastOneDenied: (List<String>) -> Unit = {},
    onAtLeastOneForeverDenied: (List<String>) -> Unit = {}
) {
    onResult(
        { acceptedPermissions -> onAllAccepted.invoke(acceptedPermissions) },
        { deniedPermissions -> onAtLeastOneDenied.invoke(deniedPermissions) },
        { foreverDeniedPermissions -> onAtLeastOneForeverDenied.invoke(foreverDeniedPermissions) }
    )
}
```

```kotlin
            Perms(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult(
                    onAllAccepted = { /* Called on all granted */ },
                    onAtLeastOneDenied = { /* Called on at least one denied */ },
                    onAtLeastOneForeverDenied = { /* Called on at least one forever denied */ }
                )
```

## License

```
     Copyright 2019 kubiakdev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
