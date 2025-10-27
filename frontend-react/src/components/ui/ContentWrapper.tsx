import { css, ClassNames } from "@emotion/react";
import type { ClassNamesArg } from "@emotion/react";

const ContentWrapper = ({
  children,
  position,
  align,
  justify,
  padding,
  margin,
  width,
  maxWidth,
  minHeight,
  height,
  gap,
  direction = "row",
  flex,
  flexValue,
  border,
  borderRadius,
  backgroundColor,
  wrap,
  customCss,
  onClick,
}: {
  children: React.ReactNode;
  position?: string;
  align?: string;
  justify?: string;
  padding?: string;
  margin?: string;
  width?: string;
  minHeight?: string;
  height?: string;
  gap?: string;
  direction?: "row" | "column";
  maxWidth?: string;
  flex?: true;
  flexValue?: string;
  backgroundColor?: string;
  border?: string;
  wrap?: string;
  borderRadius?: string;
  customCss?: ClassNamesArg;
  onClick?: () => void;
}) => {
  return (
    <ClassNames>
      {({ css, cx }) => (
        <div
          onClick={onClick}
          className={cx(
            customCss ,
            css`
             ${position ? `position: ${position};` : ""}
             ${align ? `align-items: ${align};` : ""}
             ${justify ? `justify-content: ${justify};` : ""}
             ${padding ? `padding: ${padding};` : ""}
             ${margin ? `margin: ${margin};` : ""}
             ${width ? `width: ${width};` : ""}
             ${maxWidth ? `max-width: ${maxWidth};` : ""}
             ${minHeight ? `min-height: ${minHeight};` : ""}
             ${height ? `height: ${height};` : ""}
             ${gap ? `gap: ${gap};` : ""}
             ${direction ? `flex-direction: ${direction};` : ""}
             ${border ? `border: ${border};` : ""}
             ${borderRadius ? `border-radius: ${borderRadius};` : ""}
             ${backgroundColor ? `background-color: ${backgroundColor};` : ""}

             ${flexValue ? `flex: ${flexValue};` : ""}
             ${wrap ? `flex-wrap: ${wrap};` : ""}

             ${align || justify || flex || gap || direction ? `display: flex;` : ""}
            `
          )}
        >
          {children}
        </div>
      )}
    </ClassNames>
  );
};

export default ContentWrapper;
