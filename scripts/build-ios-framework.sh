#!/usr/bin/env bash
# Builds the Shared Kotlin framework for Xcode integration from the command line.
# Usage:
#   ./scripts/build-ios-framework.sh          # physical device (iphoneos / arm64)
#   ./scripts/build-ios-framework.sh simulator
#
# embedAndSignAppleFrameworkForXcode requires Xcode environment variables that are
# normally set by Xcode or Android Studio — this script provides them.

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

PLATFORM="${1:-device}"
shift || true

export CONFIGURATION="${CONFIGURATION:-Debug}"
export EXPANDED_CODE_SIGN_IDENTITY="${EXPANDED_CODE_SIGN_IDENTITY:--}"
export CODE_SIGNING_ALLOWED="${CODE_SIGNING_ALLOWED:-NO}"
export ARCHS="${ARCHS:-arm64}"

if [[ "$PLATFORM" == "simulator" ]]; then
  export SDK_NAME="$(xcrun --sdk iphonesimulator --show-sdk-name)"
  export PLATFORM_NAME=iphonesimulator
  export EFFECTIVE_PLATFORM_NAME=-iphonesimulator
else
  export SDK_NAME="$(xcrun --sdk iphoneos --show-sdk-name)"
  export PLATFORM_NAME=iphoneos
  export EFFECTIVE_PLATFORM_NAME=-iphoneos
fi

export TARGET_BUILD_DIR="$ROOT_DIR/shared/build/xcode-frameworks/$CONFIGURATION/$SDK_NAME"
export FRAMEWORKS_FOLDER_PATH="${FRAMEWORKS_FOLDER_PATH:-Frameworks}"
export PRODUCT_NAME="${PRODUCT_NAME:-HouseBuddy}"
export FULL_PRODUCT_NAME="${FULL_PRODUCT_NAME:-$PRODUCT_NAME.app}"
export CONTENTS_FOLDER_PATH="${CONTENTS_FOLDER_PATH:-$FULL_PRODUCT_NAME}"
export EXECUTABLE_FOLDER_PATH="${EXECUTABLE_FOLDER_PATH:-$FULL_PRODUCT_NAME}"
export UNLOCALIZED_RESOURCES_FOLDER_PATH="${UNLOCALIZED_RESOURCES_FOLDER_PATH:-$FULL_PRODUCT_NAME}"
export BUILT_PRODUCTS_DIR="${BUILT_PRODUCTS_DIR:-$TARGET_BUILD_DIR}"

mkdir -p "$TARGET_BUILD_DIR"

echo "Building Shared.framework for $PLATFORM ($SDK_NAME, $ARCHS, $CONFIGURATION)..."

./gradlew :shared:embedAndSignAppleFrameworkForXcode --rerun-tasks "$@"

echo "Done. Framework output: $TARGET_BUILD_DIR/Shared.framework"
