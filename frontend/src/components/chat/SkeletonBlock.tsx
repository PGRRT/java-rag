import { css, cx } from "@emotion/css";
import ContentWrapper from "@/components/ui/ContentWrapper";
import { styles } from "@/constants/styles";
import colorPalette from "@/constants/colorPalette";
import { skeletonAnimation } from "@/styles/animations";

const SkeletonBlock = ({ width = "200px", height = "45px" }: { width?: string, height?: string}) => {
  return (
    <ContentWrapper
      customCss={css`
        border-radius: ${styles.borderRadius.medium};
        background-color: ${colorPalette.backgroundBright};
        padding: ${styles.padding.small} ${styles.padding.medium};
        align-self: flex-start;

        width: ${width};
        height: ${height};
      `}
    >
      <div
        className={cx(
          skeletonAnimation,
          css`
            height: 100%;
            // height: 20px;
            width: 100%;
            border-radius: ${styles.borderRadius.small};
          `
        )}
      />
    </ContentWrapper>
  );
};

export default SkeletonBlock;
